# ITA Challenge Authentication Microservice

## Overview
The ITA Challenge Authentication Microservice is a Spring Boot application that handles user authentication through GitHub OAuth2. It provides secure authentication endpoints and integrates with other microservices in the ITA Challenge ecosystem.

## Authentication Workflow

1. **GitHub OAuth2 Flow**:
    - Client initiates authentication by requesting GitHub authorization
    - GitHub redirects back with an authorization code
    - Service exchanges code for access token
    - Service validates token with GitHub
    - Service verifies user existence in the system

2. **Token Validation Process**:
    - Validates GitHub access token
    - Retrieves GitHub user information
    - Verifies user exists in ITA Challenge database
    - Returns authentication status and user details

## API Endpoints

### Authentication Endpoints

#### POST `/itachallenge/api/v1/auth/github/authenticate`
- **Purpose**: Authenticates user with GitHub OAuth code
- **Request Body**:
  ```json
  {
    "code": "github_oauth_code"
  }
  ```
- **Response**:
  ```json
  {
    "isValid": true,
    "username": "github_username",
    "token": "access_token"
  }
  ```
- **Status Codes**:
    - 200: Successful authentication
    - 401: Invalid credentials
    - 403: User not found in database
    - 500: Internal server error

#### GET `/itachallenge/api/v1/auth/version`
- **Purpose**: Retrieves service version information
- **Response**:
  ```json
  {
    "application_name": "itachallenge-auth",
    "version": "2.0.0-RELEASE"
  }
  ```

### Test Endpoints

#### GET `/itachallenge/api/v1/auth/test`
- **Purpose**: Basic service health check
- **Response**: String message confirming service is running

#### GET `/itachallenge/api/v1/auth/call-user-test`
- **Purpose**: Tests connection with user service
- **Response**: Response from user service

## Configuration

### Environment Variables
- `GITHUB_CLIENT_ID`: GitHub OAuth application client ID
- `GITHUB_CLIENT_SECRET`: GitHub OAuth application client secret
These variables are provided by the ITA Challenge team.

## Docker Deployment

### Prerequisites
- Docker installed
- GitHub OAuth credentials
- Access to Docker registry

### Building the Docker Image

1. Set required environment variables:
```bash
export ENV=dev REGISTRY_NAME=itacademybcn/itachallenges MICROSERVICE_VERSION=x.x.x GITHUB_CLIENT_ID={your_github_client_id} GITHUB_CLIENT_SECRET={your_github_client_secret}
```

2. Run the build script:
```bash
./itachallenge-auth/build_Docker.sh
```

The script will:
- Clean and build the application using Gradle
- Build Docker image with GitHub credentials
- Push the image to Docker registry (for dev/pre environments)

## Testing Docker Containers

### 1. Find Container's IP Address
To find the IP address of the container you want to call:
```bash
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' CONTAINER_ID
```
Replace `CONTAINER_ID` with your actual container ID.

### 2. Install curl in the Calling Container
Access the terminal (Exec tab) of the container making the call and install curl:

```bash
# Update package repositories
apk update

# Install curl
apk add curl

# Verify curl installation
curl --version
```

### 3. Make API Calls
Format for API calls:
```bash
curl http://{CONTAINER_IP}:{PORT}/{ENDPOINT}
```

Where:
- `CONTAINER_IP`: IP address obtained from step 1
- `PORT`: Port from docker/apisix_conf/apisix_standalone_dev.yaml (e.g., auth: 8761, user: 8764)
- `ENDPOINT`: The API endpoint you want to test

Example:
```bash
# Testing auth endpoint
curl http://172.17.0.4:8761/itachallenge/api/v1/auth/test
```

## Port Reference
- Auth Service: 8761
- User Service: 8764

## Development

### Building Locally
```bash
./gradlew clean build
```

### Running Auth Microservice
```bash
./gradlew itachallenge-auth:bootRun
```

