#!/bin/sh

uuid_user="$USER_ID"
uuid_challenge="$CHALLENGE_ID"
uuid_language="$LANGUAGE_ID"
uuid_solution="$SOLUTION_ID"
status=0
user_code_message=""
user_code_errors=""
sandbox_exceptions=""
exceptions=""

output_json() {
  jq -n \
    --arg uuid_user "$uuid_user" \
    --arg uuid_challenge "$uuid_challenge" \
    --arg uuid_language "$uuid_language" \
    --arg uuid_solution "$uuid_solution" \
    --argjson status "$status" \
    --arg user_code_message "$user_code_message" \
    --arg user_code_errors "$user_code_errors" \
    --arg sandbox_exceptions "$sandbox_exceptions" \
    --arg exceptions "$exceptions" \
    '{
      uuid_user: $uuid_user,
      uuid_challenge: $uuid_challenge,
      uuid_language: $uuid_language,
      uuid_solution: $uuid_solution,
      status: $status,
      user_code_message: $user_code_message,
      user_code_errors: $user_code_errors,
      sandbox_exceptions: $sandbox_exceptions,
      exceptions: $exceptions
    }' > /data/output/sandboxResults${SOLUTION_ID}.json

  rm -f "$clean_javaFile" "$parametersFile" "${clean_javaFile%.java}.class"
}

missing_vars=""
for var in USER_ID CHALLENGE_ID LANGUAGE_ID SOLUTION_ID; do
  if [ -z "$(eval echo \$$var)" ]; then
    missing_vars="$missing_vars $var"
  fi
done

if [ -n "$missing_vars" ]; then
  sandbox_exceptions="Missing environment variables:$missing_vars."
  output_json
  exit 1
fi

javaFile="/data/input/SolutionBody${SOLUTION_ID}.java"
parametersFile="/data/input/parameters${SOLUTION_ID}.txt"

if [ ! -f "$javaFile" ]; then
  sandbox_exceptions="Java file $javaFile not found."
  output_json
  exit 1
fi

if [ ! -f "$parametersFile" ]; then
  sandbox_exceptions="Parameter file $parametersFile not found."
  output_json
  exit 1
fi

if ! command -v javac > /dev/null 2>&1; then
  sandbox_exceptions="JDK not found or not configured in PATH."
  output_json
  exit 1
fi

if ! command -v java > /dev/null 2>&1; then
  sandbox_exceptions="JRE not found or not configured in PATH."
  output_json
  exit 1
fi

clean_solution_id=$(echo "$SOLUTION_ID" | tr -d '-')
clean_javaFile="/data/input/SolutionBody${clean_solution_id}.java"

if [ "$javaFile" != "$clean_javaFile" ]; then
  mv "$javaFile" "$clean_javaFile"
fi

javac "$clean_javaFile"
if [ $? -ne 0 ]; then
  status=0
  user_code_message="Code doesn't compile."
  output_json
  exit 1
fi

status=3
user_code_message="Code working as expected."

while IFS='=' read -r key value; do
  if [ -z "$key" ] || [ -z "$value" ]; then
    continue
  fi

  resultado=$(java -cp /data/input "SolutionBody${clean_solution_id}" "$key")
  if [ $? -ne 0 ]; then
    status=1
    user_code_message="Code compiles but doesn't work."
    user_code_errors="Execution failed for key $key."
    break
  fi

  if [ "$resultado" != "$value" ]; then
    status=2
    user_code_message="Code is working but not all outputs are as expected."
    user_code_errors="Expected $value but got $resultado for key $key."
  fi
done < "$parametersFile"

output_json
