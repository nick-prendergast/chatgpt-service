# ChatGPT Web Application

A Spring Boot service that provides both a web interface and REST API for interacting with OpenAI's GPT models. This service offers:

Chat functionality through GPT-3.5-turbo
Audio transcription using Whisper
Simple web UI for direct interactions
REST API for programmatic access
JWT-based authentication for API endpoints

## Prerequisites

### OpenAI API Key
- **Required**: Obtain from [OpenAI Platform](https://platform.openai.com/account/api-keys)
- Set as environment variable: `OPENAI_API_KEY`

```bash
export OPENAI_API_KEY=your_openai_api_key_here
```

## Running the Application

### Setup
1. Clone the repository
2. Set OpenAI API Key
3. Run with Maven:
   ```bash
   mvn spring-boot:run
   ```

## Access Methods

### 1. Web Interface
- **URL**: `http://localhost:8500`
- Public access
- Direct chat with GPT-3.5-turbo

### 2. Postman API Access
- **Base URL**: `http://localhost:8500`

## Endpoints

### Authentication
- **POST** `/api/v1/auth/login`
- Default Credentials:
    - Username: `devuser`
    - Password: `devpass123`

### Chat API
- **POST** `/api/v1/chat`
- Requires JWT token
- Request Body:
  ```json
  {
    "question": "Your message here"
  }
  ```

### Transcription
- **POST** `/api/v1/transcription`
- Multipart form data
- Requires JWT token

## Configuration

### Environment Variables
- `OPENAI_API_KEY`: OpenAI API key (REQUIRED)

## System Details
- **Port**: 8500
- **OpenAI Model**: GPT-3.5-turbo
- **JWT Token Expiration**: 24 hours