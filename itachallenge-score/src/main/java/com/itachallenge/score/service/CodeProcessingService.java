package com.itachallenge.score.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Volume;
import com.itachallenge.score.dto.ScoreRequest;
import com.itachallenge.score.dto.ScoreResponse;
import com.itachallenge.score.filter.Filter;
import com.itachallenge.score.util.ExecutionResult;
import com.itachallenge.score.domain.ScoreResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;

import static com.github.dockerjava.api.model.HostConfig.newHostConfig;
import static com.itachallenge.score.domain.ScoreResult.fromTerminalOutput;
import static com.itachallenge.score.dto.ScoreResponse.INTERNAL_SERVER_ERROR_RESPONSE;
import static com.itachallenge.score.dto.ScoreResponse.SOLUTION_TEXT_FILTER_FAILED_RESPONSE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import static java.nio.file.Paths.get;
import static java.nio.file.Files.newBufferedWriter;


@Service
@Primary
final class CodeProcessingService implements CodeProcessingManager {

    // Constants

    // ONLY for testing purposes, it is the name of the script mock we use to mock an output from the container
    private static final String SCRIPT_MOCK_FILENAME = "processUserCode.sh";


    // Configuration properties

    private String imageName;

    private String storagePath;

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
    public ResponseEntity<ScoreResponse> processCode(ScoreRequest scoreRequest) {

        // Pull the image from registry
        pullImage();

        // Create the container based on the image
        CreateContainerResponse container = createContainer();

        // Filter the user code
        ExecutionResult executionResult = filterChain.apply(scoreRequest.getSolutionText());

        // Precondition: User provided code must pass all the filters
        if (!executionResult.isSuccess()) {

            cleanContainer(container);

            return status(UNPROCESSABLE_ENTITY)
                    .body(SOLUTION_TEXT_FILTER_FAILED_RESPONSE);

        }

        // Create a java file in the appropriate folder using the user provided code
        ResponseEntity<ScoreResponse> responseEntity = createJavaFile(scoreRequest);

        // This checks if there has been an IOException during the creation of the java file
        if (responseEntity != null)
            return responseEntity;

        // IMPORTANT: This method is meant for testing purposes ONLY. Please, read the documentation in
        // the method's implementation.
        ExecCreateCmdResponse execCreateCmdResponse = getExecCreateCmdResponseFromMockedScript(container);

        // IMPORTANT: This method contains the actual logic to handle a script inside the container, it is not tested,
        // and it is meant to substitute the one above in production. Using this instead of the mocked one above is
        // HIGHLY DISCOURAGED.
        // ExecCreateCmdResponse execCreateCmdResponse = executeScriptInContainer(container);

        // Get the container's terminal output
        ByteArrayOutputStream byteArrayOutputStream = writeTerminalOutputToStream(execCreateCmdResponse);

        // TODO from the output stream the response should be built and returned (donne)

        // Process the container output and return the response
        return processContainerOutput(byteArrayOutputStream);

    }


    // Helper methods

    private ByteArrayOutputStream writeTerminalOutputToStream(ExecCreateCmdResponse execCreateCmdResponse) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {

            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(getResultCallback(outputStream)).awaitCompletion();

        } catch (InterruptedException e) {
            throw new RuntimeException(e); // TODO This needs to be clearly improved with logging and a proper return
        }

        return outputStream;

    }

    private ExecCreateCmdResponse getExecCreateCmdResponseFromMockedScript(CreateContainerResponse container) {

        // IMPORTANT: This is for mocking purposes ONLY. The script generated has no actual code and just mocks an
        // output in the container's terminal. The formal of the output is not final either, it may change over time.
        createScriptFile(SCRIPT_MOCK_FILENAME);


        // Execute script in container
        // IMPORTANT: This uses the mocked script created above, should be substituted by the non "stubbed" one in production
        return executeScriptInContainerMock(container);

    }

    private CreateContainerResponse createContainer() {

        return dockerClient
                .createContainerCmd(imageName)
                .withVolumes(new Volume(storagePath))
                .withHostConfig(newHostConfig()
                        .withBinds(new Bind(storagePath, new Volume(remoteVolumePath))))
                .exec();

    }

    private void pullImage() {

        try {

            dockerClient
                    .pullImageCmd(imageName)
                    .start()
                    // TODO Auth is necessary here, in order to do it we need to create an AuthConfig
                    .awaitCompletion();

        } catch (InterruptedException e) {
            throw new RuntimeException(e); // TODO This needs proper logging and a proper return
        }

    }

    private ResponseEntity<ScoreResponse> createJavaFile(ScoreRequest scoreRequest) {
        try {
            JavaFileService.createJavaFile(scoreRequest.getSolutionText(), extractFilenameFromUserSolutionPath(), storagePath);
        } catch (IOException e) {
            return getInternalServerErrorScoreResponseResponseEntity(e.getMessage());
        }
        return null;
    }

    private ResponseEntity<ScoreResponse> processContainerOutput(ByteArrayOutputStream outputStream) {

        String output = outputStream.toString();

        try {
            // Determina el resultado basado en el output del terminal
            ScoreResult scoreResult = fromTerminalOutput(output);
            ScoreResponse scoreResponse = getScoreResponseFromScoreResult(scoreResult); // Construye la respuesta
            // TODO we need to add this line to send the response back to ZMQ Cliente.
            //  zmqClient.send(scoreResponse); // Send the response to the ZMQ Client
            return ok(scoreResponse);

        } catch (IllegalArgumentException e) {
            return getInternalServerErrorScoreResponseResponseEntity(e.getMessage());
        }
    }

    private static @NotNull ScoreResponse getScoreResponseFromScoreResult(ScoreResult scoreResult) {
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScore(scoreResult.getScore());
        scoreResponse.setCompilationMessage(scoreResult.getDescription());
        return scoreResponse;
    }

    private static @NotNull ResponseEntity<ScoreResponse> getInternalServerErrorScoreResponseResponseEntity(String errorMessage) {

        ScoreResponse scoreResponse = INTERNAL_SERVER_ERROR_RESPONSE;

        scoreResponse.setCompilationMessage(errorMessage);

        return status(INTERNAL_SERVER_ERROR).body(scoreResponse);

    }

    private void cleanContainer(CreateContainerResponse container) {
        dockerClient.stopContainerCmd(container.getId()).exec();
        dockerClient.removeContainerCmd(container.getId()).exec();
    }

    private @NotNull String extractFilenameFromUserSolutionPath() {

        // TODO This should be adapted since it will only work for testing purposes with the current path string
        return get(storagePath).getFileName().toString();

    }

    private static ResultCallback.@NotNull Adapter<Frame> getResultCallback(ByteArrayOutputStream outputStream) {

        return new ResultCallback.Adapter<>() {
            @Override
            public void onNext(Frame frame) {

                try {
                    outputStream.write(frame.getPayload());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

    }

    private ExecCreateCmdResponse executeScriptInContainerMock(CreateContainerResponse container) {

        final String pathInContainer = storagePath.split(":")[1];

        String fileName = extractFilenameFromUserSolutionPath();

        String scriptPathInContainer = get(pathInContainer, SCRIPT_MOCK_FILENAME).toString();
        String filePathInContainer = get(pathInContainer, fileName).toString();

        return dockerClient.execCreateCmd(container.getId())
                .withCmd("ash", "-c", scriptPathInContainer + " " + filePathInContainer)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();

    }

    private void createScriptFile(String fileName) {

        // Juts for mocking purposes, this method is meant to be deleted
        String scriptContent = """
                #!/bin/sh
                
                if [ "$2" = "0" ]; then
                    echo "Couldn't compile" >&2
                    echo "Score: 0" >&2
                    exit 1
                elif [ "$2" = "1" ]; then
                    echo "Code compiled successfully, but some errors were detected."
                    echo "Score: 50"
                    exit 0
                elif [ "$2" = "2" ]; then
                    echo "Code compiled and executed successfully."
                    echo "Tests passed."
                    echo "Score: 75"
                    exit 0
                else
                    echo "Invalid parameter." >&2
                    exit 1
                fi
                """;

        Path scriptPath = get(storagePath, fileName);

        try (BufferedWriter writer = newBufferedWriter(scriptPath)) {

            writer.write(scriptContent);

        } catch (IOException e) {
            // TODO This needs proper logging and/or exception handling
            e.printStackTrace();
        }

    }

}
