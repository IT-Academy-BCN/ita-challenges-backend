#!/bin/bash
echo "Environment Variables:" env

# Ensure the SOLUTION_ID is set
if [ -z "$SOLUTION_ID"]; then
  echo "SOLUTION_ID environment variable is not set!"
  exit 1
fi

# Define the fie paths based on volumePath
parameters_file="/data/${SOLUTION_ID}_parameters.txt"
solution_file="/data/SolutionBody_${SOLUTION_ID}.java"

# Check if the parameters file exists
if [ -f "$parameters_file" ]; then
  echo "Parameters file found: $parameters_file"
else
  echo "Parameters file not found!"
  exit 1
fi

# Check if the solution fie exists
if [ -f "$solution_file" ]; then
  echo "Solution file found: $solution_file"
else
  echo "Solution file NOT found!: $solution_file"
  exit 1
fi

# Read key/value pairs from the parameters file
declare -A params
while IFS='=' read -r key value; do
  params[$key]="$value"
done < "$parameters_file"

# Compile the user code
javac "$solution_file"

# check if compilation was successful
if [ $? -eq 0 ]; then
  # Run the compiled code with each input key ans checks the output
  class_name=$(basename "$solution_file" .java)

  for input_key in "${!params[@]}"; do
    expected_output="${params[${input_key}]}"

    echo "Testing input: ${input_key}"
    actual_output=$(java "$class_name" "${input_key}")

    echo "Expected output: ${expected_output}"
    echo "Actual output: ${actual_output}"

    if [ "$expected_output" == "$actual_output" ]; then
      echo "Test passed!"
    else
      echo "Test failed!"
    fi
  done
else
  echo "Compilation failed!"
fi