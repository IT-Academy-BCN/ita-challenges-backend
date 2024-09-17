#!/bin/bash

# List of classes to remove with their paths
classes=(
  "/opt/custom-jdk/lib/modules/java/lang/System.class"
  "/opt/custom-jdk/lib/modules/java/lang/Runtime.class"
  "/opt/custom-jdk/lib/modules/java/io/FileInputStream.class"
  "/opt/custom-jdk/lib/modules/java/io/PrintStream.class"
  "/opt/custom-jdk/lib/modules/java/io/File.class"
  "/opt/custom-jdk/lib/modules/java/io/FileReader.class"
  "/opt/custom-jdk/lib/modules/java/io/FileWriter.class"
  "/opt/custom-jdk/lib/modules/java/io/BufferedReader.class"
  "/opt/custom-jdk/lib/modules/java/io/BufferedWriter.class"
  "/opt/custom-jdk/lib/modules/java/io/InputStream.class"
  "/opt/custom-jdk/lib/modules/java/io/OutputStream.class"
  "/opt/custom-jdk/lib/modules/java/io/RandomAccessFile.class"
  "/opt/custom-jdk/lib/modules/java/io/FileOutputStream.class"
  "/opt/custom-jdk/lib/modules/java/io/FileDescriptor.class"
)

# Remove the specified classes
rm -f "${classes[@]}"