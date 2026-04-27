# Pulse Backend (Spring Boot + MySQL)

This backend is built to work with your existing Pulse frontend without changing frontend code.

## Tech Stack
- Java 17
- Spring Boot 3.3.5
- Spring Web, Spring Data JPA, Spring Validation, Spring Security
- MySQL

## Implemented Requirements
- Frontend validation and error handling support
  - Bean validation on request payloads
  - Clear plain-text error responses for failed requests (compatible with frontend `response.text()` flow)
- Fetch/Axios-compatible REST APIs
  - JSON success responses
  - Proper HTTP status codes
  - CORS support for separate frontend and backend links
- Authentication
  - `POST /api/auth/register`
  - `POST /api/auth/login`
- User management APIs used by frontend admin page
  - `GET /api/users`
  - `POST /api/users`
  - `PUT /api/users/{id}`
  - `DELETE /api/users/{id}`
  - `PATCH /api/users/{id}/access?active=true|false`

## Default Admin Seed
At first startup, backend seeds:
- username: `admin`
- password: `admin123`
- role: `admin`

## Environment Variables
Set these in local/dev or your deployment platform:

- `PORT` (default: `8080`)
- `DB_URL` (default: `jdbc:mysql://localhost:3306/pulse_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`)
- `DB_USERNAME` (default: `root`)
- `DB_PASSWORD` (required)
- `DDL_AUTO` (default: `update`)
- `SHOW_SQL` (default: `false`)
- `CORS_ALLOWED_ORIGINS` (default: `http://localhost:5173`)

For separate frontend/backend deployments, set `CORS_ALLOWED_ORIGINS` to your frontend URL.
Example:

`CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com`

## Run Locally

### PowerShell setup for your MySQL Workbench local instance

Use these commands exactly in PowerShell. The password is quoted so `#` and spaces are preserved.

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/pulse_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="#Om ps 78"
$env:CORS_ALLOWED_ORIGINS="http://localhost:5173"
./mvnw.cmd spring-boot:run
```

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```

Backend runs at `http://localhost:8080` by default.

## Build

```bash
# Windows
mvnw.cmd -DskipTests package

# macOS/Linux
./mvnw -DskipTests package
```

Jar path:
- `target/pulse-backend-0.0.1-SNAPSHOT.jar`

## Frontend Integration
Set frontend env variable to backend URL:

`VITE_API_BASE_URL=https://your-backend-domain.com`

This keeps frontend link and backend link fully separate.

## Deployment (Separate Frontend and Backend Links)

Set these backend environment variables in your hosting platform:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com`

Set this in frontend deployment:
- `VITE_API_BASE_URL=https://your-backend-domain.com`
