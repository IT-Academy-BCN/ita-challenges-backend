# How to create a custom JRE from Windows

This document outlines the steps to create a custom JRE using ```jlink``` from Windows.
The entire process is performed within a Docker container, enabling you to generate an optimized JRE compatible with **Alpine Linux**, the operating system used in the **Sandbox** where the user's code will be executed and tested.

## **1. Start a Docker container with OpenJDK**

The first step is to create a new container using the official OpenJDK image to set up an environment where you can customize the JRE.
Open the terminal and run the following command:

```bash
docker run -it --rm openjdk:21-jdk /bin/bash
```

This command will start a container based on OpenJDK 21 with an interactive shell.
If everything goes well, you’ll be inside the container. Using the ```--rm``` flag ensures the container will be automatically removed when you exit.

## **2. Create the custom JRE with jlink**

Inside the container, generate the custom JRE by following these steps:

#### 2.1. Identify the required modules

Select the modules you want to include in the custom JRE.
For example, the following modules were selected for the initial custom JRE for the project. You may need to add or exclude others as required:

- java.base
- java.logging
- java.prefs
- java.rmi
- java.sql
- jdk.charsets
- java.desktop

#### 2.2. Run the jlink command

Use ```jlink``` to generate the custom JRE by running the following command inside the container:

```bash
jlink --module-path $JAVA_HOME/jmods \
--add-modules java.base,java.logging,java.prefs,java.rmi,java.sql,jdk.charsets,java.desktop \
--strip-debug --compress=2 --no-header-files --no-man-pages \
--output /custom-jre
```

This will create a custom JRE with the specified modules in the ```/custom-jre``` directory (inside the container). You can modify the command to add or exclude modules or change the output directory path if needed.

## **3. Copy the generated JRE to your machine**

Make sure to copy the custom JRE to your system before destroying the container, as the JRE will be lost too once the container is deleted.
Follow these steps to transfer the custom JRE:

#### 3.1. Compress the JRE inside the container

Run the following command to create a compressed archive of the JRE for easier transfer:

```bash
tar -czvf /custom-jre.tar.gz /custom-jre
```

#### 3.2. Identify the container ID

Open a **new terminal** (without closing the current one) and verify the ID of your running container using the following command:

```bash
docker ps
```

This command provides a detailed list of running containers. Copy the ID of the container where you created the custom JRE.

#### 3.3. Copy the compressed file from the container to your machine

**From the new terminal**, run the following command to copy the compressed file from the container to your computer (replace ```<container_id>``` with the actual container ID you copied before and ```<path>``` with the desired destination path):

```bash
docker cp <container_id>:/custom-jre.tar.gz <path>
```

Example:

```bash
docker cp 800a361bc05c:/custom-jre.tar.gz C:\Users\Michel\Desktop\custom-jre
```

#### 3.4. Decompress the ```custom-jre.tar.gz``` file on your machine

Use one of the following methods to extract the file:

1. Using the terminal:

Run the following command **from the new terminal** (adjust paths as needed):

```bash
tar -xvzf "compressed_file_path" -C "output_path"
```

Example:

```bash
tar -xvzf "C:\Users\Michel\Desktop\custom-jre\custom-jre.tar.gz" -C "C:\Users\Michel\Desktop\custom-jre"
```

2. Using 7-Zip:

- Right-click on the ```custom-jre.tar.gz``` file.
- Select "7-Zip > Extract Here". This will extract the ```custom-jre.tar``` file.
- Repeat the process for the ```custom-jre.tar``` file to obtain the final ```custom-jre``` folder.

#### 3.5.  Verify the modules in your custom JRE

Navigate to the ```custom-jre\legal``` folder and check that all the required modules are included.

## **4. Finalize and clean up**

If everything looks correct, so you don’t need to recreate the JRE, return to the previous terminal (inside the container) and run the ```exit``` command.
Exiting the container will automatically delete it due to the ```--rm``` flag used when creating the container.

## **Additional notes**

- Ensure you have the necessary permissions on your system to execute Docker commands and extract files properly.

- This process can be repeated as many times as needed if you need to create a new JRE or adjust the results.

- When adding the custom JRE to your project, do not forget to check all the files in the JRE directory and subdirectories. Some files may be automatically added to ```.gitignore```. If this happens, right-click the file in your IDE and select "Git > Add" to include it in the repository.