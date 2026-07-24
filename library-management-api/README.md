# Assignment 2 — Library Management REST API

This Spring Boot REST API continues the Library Management System from Assignment 1. It connects to MySQL and can be managed with MySQL Workbench.

## Assignment 1 database mapping

| Existing table | Existing primary key | Spring entity |
|---|---|---|
| `admin` | `admin_id` | `Admin` |
| `students` | `student_id` | `Student` |
| `books` | `book_id` | `Book` |
| `borrow` | `borrow_id` | `Borrow` |

The original `books.stutus` spelling is intentionally preserved because it exists in the Assignment 1 schema. Book availability is synchronized from `quantity`. Borrow status is derived by the API from `due_date` and `return_date`; it is not stored as a new database column.

## Requirements

- Java 17 or newer
- MySQL 8+ and, optionally, MySQL Workbench
- A MySQL user with permission to create or access `librarymanagementsystem`
- Internet access on the first Maven-wrapper run

## Database setup

Start MySQL and make sure your user can create databases. By default, the JDBC connection creates the `librarymanagementsystem` database when it does not exist. The application then uses Hibernate to create or update its tables.

The default local connection is:

```text
Host: localhost
Port: 3306
Database: librarymanagementsystem
Username: root
Password: newpassword
```

Override those values when necessary:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your-password"
$env:DB_URL="jdbc:mysql://localhost:3306/librarymanagementsystem?createDatabaseIfNotExist=true&serverTimezone=UTC"
```

Hibernate uses `ddl-auto=update`, so it creates missing tables and updates the structure without deleting existing data.

## Run and test

```powershell
.\mvnw.cmd spring-boot:run
```

The API starts at `http://localhost:8081` by default. Set the `SERVER_PORT`
environment variable to use a different port.

### Test with Swagger UI

After starting the API, open:

**http://localhost:8081/swagger-ui.html**

Swagger lists every endpoint and fills request bodies with example values. To test one:

1. Expand an endpoint such as `POST /api/books`.
2. Click **Try it out**.
3. Edit the example JSON if needed.
4. Click **Execute** to send the request and view the response.

The raw OpenAPI JSON is available at `http://localhost:8081/v3/api-docs`.

```powershell
.\mvnw.cmd test
```

Tests use an isolated H2 database and do not modify Assignment 1 data.

## REST endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/login` | Admin or student login |
| GET/POST | `/api/admins` | List or create admins |
| GET/PUT/DELETE | `/api/admins/{id}` | Read, update, or delete an admin |
| GET | `/api/admins/dashboard` | View totals and active/overdue loans |
| GET | `/api/admins/borrow-history` | View all borrowing history |
| GET/POST | `/api/students` | List or create students |
| GET/PUT/DELETE | `/api/students/{id}` | Read, update, or delete a student |
| GET/POST | `/api/books` | Search/list or create books |
| GET/PUT/DELETE | `/api/books/{id}` | Read, update, or delete a book |
| GET/POST | `/api/borrows` | List loans or borrow a book |
| GET | `/api/borrows?studentId=1` | View one student's borrowing history |
| POST | `/api/borrows/{id}/return` | Return a borrowed book |

## Request examples

Admin login using the Assignment 1 account:

```json
{
  "role": "ADMIN",
  "username": "admin",
  "password": "12345678"
}
```

Student login using the numeric Assignment 1 student ID:

```json
{
  "role": "STUDENT",
  "studentId": 1,
  "password": "123456"
}
```

Create a student:

```json
{
  "name": "Sok Dara",
  "email": "dara2@example.com",
  "phone": "012345678",
  "password": "student123"
}
```

Create a book:

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "category": "Software Engineering",
  "quantity": 3
}
```

Borrow a book:

```json
{
  "studentId": 1,
  "bookId": 1,
  "dueDate": "2026-08-15"
}
```

The API accepts existing Assignment 1 plaintext passwords for compatibility. Passwords created or changed through this API are stored as BCrypt hashes and are never returned in responses. Credential checking is implemented, but JWT endpoint authorization is outside the current project scope.
