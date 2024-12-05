#!/bin/bash

echo "Environment Variables:"
env

# Ensure the SOLUTION_ID is set
if [ -z "$SOLUTION_ID" ]; then
  echo "SOLUTION_ID environment variable is not set!"
  exit 1
fi

# Define the file paths based on volumePath
parameters_file="/data/${SOLUTION_ID}_parameters.txt"
solution_file="/data/SolutionBody_${SOLUTION_ID}.java"
temp_solution_file="/data/SolutionBody_${SOLUTION_ID//-/_}.java"
json_output_file="data/${SOLUTION_ID}_output.json"

# Initialize json fields
status=0
user_code_message=""
user_code_errors=""
sandbox_exceptions=""
exceptions=""

# Check if the parameters file exists
if [ -f "$parameters_file" ]; then
  echo "Parameters file found: $parameters_file"
else
  echo "Parameters file not found!"
  echo "{\"status\": $status, \"user_code_message\": \"$user_code_message\", \"user_code_errors\": \"$user_code_errors\", \"sandbox_exceptions\": \"$sandbox_exceptions\", \"exceptions\": \"$exceptions\"}" > "$json_output_file"
  exit 1
fi

# Check if the solution file exists
if [ -f "$solution_file" ]; then
  echo "Solution file found: $solution_file"
else
  echo "Solution file not found!: $solution_file"
  echo "{\"status\": $status, \"user_code_message\": \"$user_code_message\", \"user_code_errors\": \"$user_code_errors\", \"sandbox_exceptions\": \"$sandbox_exceptions\", \"exceptions\": \"$exceptions\"}" > "$json_output_file"
  exit 1
fi

# Copy the solution file to a temporary file with underscores instead of hyphens
cp "$solution_file" "$temp_solution_file"

#Replace hypehns with underscores in the class name within the file
sed -i "s/public class .*/public class SolutionBody_${SOLUTION_ID//-/_} {/" "$temp_solution_file"

# Verify the contents of the temporary solution file
echo "Contents of $temp_solution_file:"
cat "$temp_solution_file"

# Read key/value pairs from the parameters file
declare -A params
while IFS='=' read -r key value; do
  params[$key]="$value"
done < "$parameters_file"

# Compile the user code
javac -d /data "$temp_solution_file"

# check if compilation was successful
if [ $? -eq 0 ]; then
  echo "Compilation succeeded."
  status=2 #Assume the code compiles and works nit not all outputs are correct as starting point

  # Extract the class name from te solution file name
  class_name=$(basename "$temp_solution_file" .java)

  # Run the compiled code with each input key ans checks the output
  for input_key in "${!params[@]}"; do
    expected_output="${params[${input_key}]}"

    echo "Testing input: ${input_key}"
    actual_output=$(java -cp /data "$class_name" "${input_key}")

    echo "Expected output: ${expected_output}"
    echo "Actual output: ${actual_output}"

    if [ "$expected_output" == "$actual_output" ]; then
      echo "Test passed!"
    else
      echo "Test failed!"
      user_code_errors="$user_code_errors\nExpected: $expected_output, Actual: $actual_output"
      status=2 # Not all outputs are correct
    fi
  done
else
  echo "Compilation failed!"
  status=0
  user_code_message="Compilation Error"
  exceptions=$(javac "$temp_solution_file" 2>&1)
fi

# Write output to JSON file
echo "{\status\": $status, \"user_code_message\": \"$user_code_message\", \"user_code_errors\": \"$user_code_errors\", \"sandbox_exceptions\": \"$sandbox_exceptions\", \"exceptions\": \"$exceptions\"}" > "$json_output_file"

# Verify JSON file creation
echo "Verifiying JSON file creation:"
ls -l /data

# Clean up compiled class files
echo "Cleaning up temporary files..."
rm "$temp_solution_file"
rm "/data/${class_name}.class"