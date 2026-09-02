# Task Tracker Monorepo

Backend practice project: a distributed task tracker built as two independent Spring Boot microservices.

The project targets the basic level of the assignment ("Хорошо" / pass).

## Services

| Service | Port | Database | Purpose |
| --- | --- | --- | --- |
| `user_service` | `8081` | H2 in-memory | Stores users |
| `task_service` | `8082` | H2 in-memory | Stores tasks |

Each service has its own database. `task_service` does not read or write the user database directly. User checks are done only through HTTP requests to `user_service`.

## API

### User Service

- `POST /users` - create a user
- `GET /users/{userId}` - get a user by ID

Swagger UI:

- `http://localhost:8081/swagger-ui/index.html`

### Task Service

- `POST /tasks` - create a task
- `GET /tasks?userId={id}` - get tasks created by or assigned to a user
- `POST /tasks/{taskId}/delegate?assigneeId={id}` - delegate a task to another user

Swagger UI:

- `http://localhost:8082/swagger-ui/index.html`

## Run Locally

Requirements:

- Java 17+
- Maven 3+

Start `user_service`:

```powershell
cd user_service
mvn spring-boot:run
```

Start `task_service` in another terminal:

```powershell
cd task_service
mvn spring-boot:run
```

H2 databases are in-memory, so data is reset after service restart.

## Manual Test Scenario

1. Create a user in `user_service`:

```json
{
  "firstName": "Ivan",
  "lastName": "Ivanov",
  "email": "ivan.demo@example.com"
}
```

2. Create a task in `task_service`.

For a clean delegation demo, create the task without `assigneeId`:

```json
{
  "title": "Prepare report",
  "creatorId": 1
}
```

3. Delegate the task:

```text
POST /tasks/1/delegate?assigneeId=1
```

Expected result: the task response contains the selected `assigneeId`.

4. Check tasks for the user:

```text
GET /tasks?userId=1
```

## Tests

Run checks for each service:

```powershell
cd user_service
mvn -B clean verify
```

```powershell
cd task_service
mvn -B clean verify
```

The same checks run in GitHub Actions.

## Documentation

Project diagrams are stored in `docs/`:

- `use-case-diagram.puml`
- `delegate-task-sequence.puml`
- `er-diagram.puml`
- `PROMPTS.md`
