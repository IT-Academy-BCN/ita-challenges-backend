#!/bin/bash

# Example user code file (this is just a placeholder, real user code would be injected or provided)
echo "public class UserCode { public static void main(String[] args) { System.out.println(\"Hello from Sandbox!\"); } }" > UserCode.java

# Compile the user code
javac UserCode.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    # Run the compiled code
    java UserCode
else
    echo "Compilation failed!"
fi
