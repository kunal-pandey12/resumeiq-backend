# ResumeIQ

AI-powered resume analyzer and interview coach built with Java and Spring Boot. Users upload a resume, and the application uses Google Gemini AI to score it against ATS standards, match it against a job description, and generate personalized interview questions.

## Features

- **Secure Authentication** — User registration and login with Spring Security and JWT. Passwords are hashed with BCrypt, and every protected endpoint requires a valid token.
- **Role-Based Access Control** — Users are assigned a `USER` or `ADMIN` role, controlling access to different parts of the application.
- **Resume Upload & Parsing** — Users upload a resume in PDF format. The app extracts the text using Apache PDFBox and stores it in the database.
- **AI-Powered ATS Scoring** — The extracted resume text is sent to Gemini AI, which returns an ATS score out of 100 along with strengths, weaknesses, and improvement suggestions.
- **Job Description Matching** — Users can paste a job description, and the app compares it against their resume, returning a match percentage, matching skills, missing skills, and suggestions.
- **AI-Generated Interview Questions** — Based on the resume content, Gemini AI generates relevant technical and HR interview questions along with suggested answers.

## Tech Stack

- **Language:** Java
- **Framework:** Spring Boot, Spring Security, Spring Data JPA / Hibernate
- **Database:** MySQL
- **Authentication:** JWT (JSON Web Token)
- **AI Integration:** Google Gemini API
- **PDF Processing:** Apache PDFBox
- **Tools:** Postman, Git, GitHub, IntelliJ IDEA

## Architecture

The project follows a layered architecture:

```
Controller  →  Service  →  Repository  →  Database
                  ↓
            GeminiService  →  Gemini AI API
```

- **Controller** — Handles incoming HTTP requests and responses.
- **Service** — Contains business logic, including resume processing and AI orchestration.
- **Repository** — Manages database access via Spring Data JPA.
- **GeminiService** — A dedicated service that communicates with the Gemini API for all AI-related features.

## Project Structure

```
com.ResumeIQ
├── config/          # Security and application configuration
├── controller/       # REST API endpoints
├── dto/               # Data Transfer Objects
├── entity/            # JPA entities (User, Resume, Role)
├── repo/              # Spring Data JPA repositories
└── service/           # Business logic and Gemini AI integration
```

## API Endpoints

| Method | Endpoint                          | Description                                  | Auth Required |
|--------|------------------------------------|-----------------------------------------------|----------------|
| POST   | `/api/auth/register`               | Register a new user                            | No             |
| POST   | `/api/auth/login`                  | Log in and receive a JWT token                 | No             |
| POST   | `/api/resume/upload`               | Upload a resume PDF and get AI-based ATS score | Yes            |
| GET    | `/api/resume/my`                   | Get all resumes uploaded by the logged-in user | Yes            |
| POST   | `/api/resume/match`                | Match a resume against a job description       | Yes            |
| GET    | `/api/resume/interview-questions`  | Generate interview questions from a resume      | Yes            |

## Setup

1. Clone the repository.
2. Create a MySQL database named `resumeiq_db`.
3. Create a `src/main/resources/application.properties` file with the following keys (this file is git-ignored for security):

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/resumeiq_db
   spring.datasource.username=root
   spring.datasource.password=YOUR_DB_PASSWORD

   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   gemini.api.key=YOUR_GEMINI_API_KEY
   gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent
   ```

4. Run the application — tables are created automatically via Hibernate.
5. Use Postman to register, log in, and test the resume upload, matching, and interview question endpoints.

## Security Notes

- Sensitive credentials (`application.properties`) are excluded from version control via `.gitignore`.
- JWT tokens are stateless — no session is stored on the server.
- Passwords are never stored in plain text; BCrypt hashing is used.

## Roadmap

- [ ] Add Docker support for containerized deployment
- [ ] Build a frontend so the app doesn't rely solely on Postman
- [ ] Add structured logging and better error handling for production use
- [ ] Add automated tests for core services
