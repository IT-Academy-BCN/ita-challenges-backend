package com.itachallenge.score.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import com.itachallenge.score.dto.zmq.ScoreRequestDto;
import com.itachallenge.score.dto.zmq.ScoreResponseDto;
import com.itachallenge.score.filter.Filter;
import com.itachallenge.score.util.ExecutionResult;
import com.itachallenge.score.domain.ScoreResult;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;

import static com.github.dockerjava.api.model.HostConfig.newHostConfig;
import static com.itachallenge.score.domain.ScoreResult.fromTerminalOutput;
import static com.itachallenge.score.dto.zmq.ScoreResponseDto.INTERNAL_SERVER_ERROR_RESPONSE;
import static com.itachallenge.score.dto.zmq.ScoreResponseDto.SOLUTION_TEXT_FILTER_FAILED_RESPONSE;
import static com.itachallenge.score.service.JavaFileService.createJavaFile;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import static java.nio.file.Paths.get;


@Service
@Primary
final class CodeProcessingService implements ICodeProcessingManager {

    // Constants

    private static final Logger logger = getLogger(CodeProcessingService.class);

    // ONLY for testing purposes, it is the name of the script mock we use to mock an output from the container
    private static final String SCRIPT_MOCK_FILENAME = "processUserCode.sh";

    // Configuration properties

    @Value("${sandbox.file.image}")
    private String imageName;
    @Value("${docker.registry.url}" )
    private  String registryUrl;
    @Value("${sandbox.file.storage.path}")
    private String storagePath;
    @Value("${sandbox.Dockerfile.volumePath}")
    private String remoteVolumePath;


    // State

    private final DockerClient dockerClient;
    private final JavaFileService javaFileService;
    private final Filter filterChain;


    // Public API

    CodeProcessingService(DockerClient dockerClient, JavaFileService javaFileService, @Qualifier("keywordFilter") Filter filterChain) {
        this.dockerClient = dockerClient;
        this.javaFileService = javaFileService;
        this.filterChain = filterChain;
    }

    @Override
    public ResponseEntity<ScoreResponseDto> processCode(ScoreRequestDto scoreRequest) {

        // Pull the image from registry
        try {
            pullImage();
        } catch (InterruptedException e) {
            return getPullingImageExceptionResponse(e);
        }

        // Filter the user code
        ExecutionResult executionResult = filterChain.apply(scoreRequest.getSolutionText());

        // Precondition: User provided code must pass all the filters
        if (!executionResult.isSuccess())
            return status(UNPROCESSABLE_ENTITY)
                    .body(SOLUTION_TEXT_FILTER_FAILED_RESPONSE);


        // Create a java file in the appropriate folder using the user provided code
        try {
            createJavaFile(scoreRequest.getSolutionText(), extractFilenameFromUserSolutionPath(), storagePath);
        } catch (IOException e) {
            return getInternalServerErrorScoreResponse(e.getMessage());
        }

        // Create the container based on the image
        CreateContainerResponse container = createContainer();

        // Start teh container without more configuration, it is all in the dockerfile
        try {
            startContainer(container.getId());
        } catch (InterruptedException e) {
            return getStartingContainerExceptionResponse(container.getId(), e);
        }

        // Process the container output and return the response
        return processContainerOutput(null);

    }

    // Helper methods


    /**
     * Creates a Docker container based on the specified image and configuration.
     *
     * <p>This method utilizes the Docker client to initiate the creation of a new container
     * using the provided Docker image. It sets up the necessary volume bindings to ensure
     * that the container has access to the required storage paths both locally and remotely.
     *
     * @return {@link CreateContainerResponse} The response containing details of the created container.
     */
    private CreateContainerResponse createContainer() {

        return dockerClient
                .createContainerCmd(imageName)
                .withVolumes(new Volume(storagePath))
                .withHostConfig(newHostConfig()
                        .withBinds(new Bind(storagePath, new Volume(remoteVolumePath))))
                .exec();

    }

    /**
     * Pulls a Docker image from the specified registry.
     * <p>
     * This method attempts to pull the Docker image using the provided Docker client.
     */
    public void pullImage() throws InterruptedException {

        logger.info("Starting to pull Docker image '{}' from registry '{}'.", imageName, registryUrl);

        dockerClient.pullImageCmd(imageName)
                .withRegistry(registryUrl)

                // Synchronous call
                .exec(new PullImageResultCallback())
                .awaitCompletion();

        logger.info("Successfully pulled Docker image '{}'.", imageName);

    }

    private ResponseEntity<ScoreResponseDto> processContainerOutput(ByteArrayOutputStream outputStream) {

        String output = outputStream.toString();

        try {
            // Determina el resultado basado en el output del terminal
            ScoreResult scoreResult = fromTerminalOutput(output);
            ScoreResponseDto scoreResponse = getScoreResponseFromScoreResult(scoreResult); // Construye la respuesta
            // TODO we need to add this line to send the response back to ZMQ Cliente.
            //  zmqClient.send(scoreResponse); // Send the response to the ZMQ Client
            return ok(scoreResponse);

        } catch (IllegalArgumentException e) {
            return getInternalServerErrorScoreResponse(e.getMessage());
        }
    }

    private static @NotNull ScoreResponseDto getScoreResponseFromScoreResult(ScoreResult scoreResult) {
        ScoreResponseDto scoreResponse = new ScoreResponseDto();
        scoreResponse.setScore(scoreResult.getScore());
        scoreResponse.setCompilationMessage(scoreResult.getDescription());
        return scoreResponse;
    }

    private static @NotNull ResponseEntity<ScoreResponseDto> getInternalServerErrorScoreResponse(String errorMessage) {

        ScoreResponseDto scoreResponse = INTERNAL_SERVER_ERROR_RESPONSE;

        scoreResponse.setCompilationMessage(errorMessage);

        return status(INTERNAL_SERVER_ERROR).body(scoreResponse);

    }

    private @NotNull String extractFilenameFromUserSolutionPath() {

        // TODO This should be adapted since it will only work for testing purposes with the current path string
        return get(storagePath).getFileName().toString();

    }

    /**
     * Starts a Docker container given its container ID.
     *
     * @param containerId The ID of the container to start.
     * @throws InterruptedException If the thread is interrupted while waiting for the container to start.
     */
    private void startContainer(String containerId) throws InterruptedException {

        logger.info("Starting Docker container with ID: {}", containerId);
        dockerClient.startContainerCmd(containerId)
                .exec();

    }

    /**
     * Returns an error response and interrupts the current thread.
     *
     * @param e the {@link InterruptedException} that aborted the pulling
     * @return the response with the predefined error body
     */
    private @NotNull ResponseEntity<ScoreResponseDto> getPullingImageExceptionResponse(InterruptedException e) {

        Thread.currentThread().interrupt();
        logger.error("Image pull operation was interrupted for image '{}'.", imageName, e);

        return status(INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR_RESPONSE);

    }

    /**
     * Returns an error response and interrupts the current thread.
     *
     * @param containerId the container's id
     * @param e  the {@link InterruptedException} that aborted the container start operation
     * @return the response with the predefined error body
     */
    private @NotNull ResponseEntity<ScoreResponseDto> getStartingContainerExceptionResponse(String containerId, InterruptedException e) {

        Thread.currentThread().interrupt();
        logger.error("Container start operation was interrupted for container '{}'.", containerId, e);

        return status(INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR_RESPONSE);

    }

}
