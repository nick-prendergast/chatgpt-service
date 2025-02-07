# ChatGPT Web Application

A Spring Boot microservice integrating with OpenAI's ChatGPT and Whisper for chat and transcription functionalities. The project includes a web interface, REST API, and JWT authentication.

---

## Prerequisites

### OpenAI API Key

- **Required**: Obtain from [OpenAI Platform](https://platform.openai.com/account/api-keys)
- Set as an environment variable:

```bash
export OPENAI_API_KEY=your_openai_api_key_here
```

---

## Running the Application

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/chatgpt-microservice-springboot.git
   cd chatgpt-microservice-springboot
   ```
2. Set OpenAI API Key as described above.
3. Build and run with Maven:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

---

## Access Methods

### 1. Web Interface

- **URL**: `http://localhost:8500`
- JSP page for direct interaction with ChatGPT.

---

## Postman API Access



### 2. Authentication Workflow

#### Obtain JWT Token

Send a **POST** request to:
```
/api/v1/auth/login
```

**Request Body (JSON):**
```json
{
  "username": "devuser",
  "password": "devpass123"
}
```

**Response:** JWT token.

#### Set Authorization Header

- Type: **Bearer Token**
- Token: **JWT received from login response**
- Apply to subsequent requests.

### 3. Example API Requests

#### Chat Endpoint

- **URL**: `http://localhost:8500/api/v1/chat`
- **Method**: POST
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer {your_jwt_token}`
- **Body (JSON):**
  ```json
  {
    "question": "Explain quantum computing"
  }
  ```

#### Transcription Endpoint

- **URL**: `http://localhost:8500/api/v1/transcription`
- **Method**: POST
- **Headers:**
    - `Authorization: Bearer {your_jwt_token}`
- **Body:** form-data
    - **Key**: `file`
    - **Value**: Select audio file (mp3/wav)

---

## Security: JWT Authentication

- Simple authentication with JWT.
- Secure API endpoints require a valid token.

---

## Configuration

### Environment Variables

| Variable         | Description             | Required |
|-----------------|-------------------------|----------|
| `OPENAI_API_KEY` | OpenAI API key | ✅ Yes |

---

## System Details

- **Port**: `8500`
- **Java Version**: `21`
- **OpenAI Model**: `GPT-3.5-turbo`
- **JWT Token Expiration**: `24 hours`
- **JSP Page**: Available for prompt input

---

