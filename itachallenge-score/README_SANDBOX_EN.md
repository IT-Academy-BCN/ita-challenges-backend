 ### Note to the compiler: This file is a sandbox for the translation of the README.md file. Please do not delete it.


This project uses Docker to create a container to run the project in isolation. Currently, an "openjdk:21" image is being used to run the project, in the future it will be changed to a custom image, capping libraries and modules that are not needed for the project to run.
Before passing the code to execution, it goes through a series of filters using the Chain of Responsibility design pattern, to ensure that the code can be executed safely.
The comparison of the results is currently hardcoded, in the future it will have to be modified by adding a repository with the expected results for each exercise UUID and language.


## Chain of Responsibility

#### UnescapeFilter
    It is responsible for undoing the character escape that the Frontend has to do to send the code to the Backend.
#### ASCIIFilter
    It is responsible for verifying that the code only contains valid ASCII characters from 0 to 127.
#### HTMLSanitizeFilter
    It is responsible for sanitizing the code for possible HTML code injections.
#### keywordFilter
    It is responsible for verifying that the code does not contain reserved words that may be dangerous for code execution.
#### SecurityFilter
    It is responsible for verifying that the code does not contain libraries or modules that may be dangerous for code execution.


## DockerExecutor

The `DockerExecutor` class is responsible for starting the container and running the user's code. This class has several responsibilities and uses several techniques to ensure that resources are managed properly.

#### Final variables

- **DOCKER_IMAGE_NAME**: Docker image to be used for project execution.
- **CONTAINER**: Name of the container to be created.
- **CODE_TEMPLATE**: Code template to be used for project execution.
    (This template cannot be parameterized due to compilation restrictions)
- **TIMEOUT_SECONDS**: Maximum execution time for user code.

#### Configuration variables

- **windowsCommand**: Shell command for Windows systems, injected via `@Value`.
- **unixCommand**: Shell command for Unix systems, injected via `@Value`.
- **isWindows**: Boolean indicating if the operating system is Windows.

#### Methods

##### execute

This method is the main entry point for running the user's code. It performs the following steps:

         1. Code Validation: Verifies if the provided Java code is null.
         2. Code Formatting: Formats the Java code using the `CODE_TEMPLATE`.
         3. Command Building: Builds the Docker command to run the code.
         4. Container Cleanup: Calls the `cleanUpContainers` method to remove any existing container with the same name.
         5. Command Execution: Uses a `ProcessBuilder` to run the command in a Docker container.
         6. Resource Management: Uses `AutoCloseableExecutor` and `AutoCloseableProcess` to ensure resources are closed properly.
         7. Exception Handling: Handles exceptions such as `TimeoutException`, `ExecutionException`, and `InterruptedException` to ensure the container is removed in case of error.
       
##### createProcessBuilder

This method creates a `ProcessBuilder` configured with the command and the appropriate shell for the operating system.

##### cleanUpContainers

This method is responsible for removing any existing container with the specified name. It performs the following steps:

         1. List Containers: Uses a `ProcessBuilder` to list existing containers.
         2. Remove Containers: Removes each found container using another `ProcessBuilder`.

##### executeCommand

This method is responsible for executing the command in the Docker container. It performs the following steps:
    
             1. Create Process Builder: Creates a `ProcessBuilder` with the command and shell.
             2. Start Process: Starts the process and waits for it to complete.
             3. Get Output: Reads the output of the process and returns it as a string.
             4. Handle Exceptions: Handles exceptions such as `IOException`, `InterruptedException`, and `TimeoutException`.

## CodeProcessingManager

The `CodeProcessingManager` class is responsible for processing the user's code. It has several responsibilities and uses several techniques to ensure that the code is processed correctly.

#### Methods

##### `processCode`

This method is the main entry point for processing the user's code. It performs the following steps:

1. **Filter Application**: Applies a chain of filters to the code to ensure it can be executed safely.
2. **Verify Compilation**: Verifies if the code compiles successfully.
3. **Execute Code**: Executes the code using the `DockerExecutor`.
4. **Handler exceptions**: Handles exceptions such as `TimeoutException`, `ExecutionException`, and `InterruptedException` to ensure the container is removed in case of error.
5. **Calculate Score**: Calculates the user's code score based on the execution result.

##### `calculateScore`

This method calculates the user's code score based on the execution result. It performs the following steps:

 1. **Verify compilation**: Verifies if the code compiles successfully.
2. **Verify execution**: Verifies if the code executes successfully.
3. **Compare results**: Compares the execution results with the expected results.
4. **Calculate score**: Calculates the user's code score based on the comparison results.
