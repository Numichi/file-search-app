# File Search Application

This is a file search application built with Spring Boot. It provides a REST API to search for files.

## Technologies Used

- **Backend:** Java 21, Spring Boot 3
- **Database:** PostgreSQL with Liquibase for migrations
- **API Documentation:** OpenAPI
- **Testing:** JUnit 5, REST Assured, Testcontainers
- **Build Tool:** Gradle

## Getting Started

### Prerequisites

- Java 21
- Podman

### Running the application

Start the application using the following command. It will build from source code and setup a Postgres database and
run two instances of the application on ports 8081 and 8082.
```bash
make run
```

The application will be available at [http://localhost:8081](http://localhost:8081) and [http://localhost:8082](http://localhost:8082).

### Example call:
#### Search for unique files with .html extension in /app folder
```bash
curl "http://localhost:8081/api/v1/unique?folder=/app&ext=html"
```
##### Example response
```json
{
    "results": [
        "AccessIssue.html",
        "AdviceController.html", 
        ...
    ],
    "errors": []
}
```

#### Get search history
```bash
curl "http://localhost:8082/api/v1/history"
```
##### Example response
```json
{
    "histories": [
        {
            "user": "user1",
            "timestamp": "2025-10-07T13:12:11.150093Z",
            "ext": "html",
            "results": [
                "AccessIssue.html",
                "AdviceController.html",
                ...
            ]
        }
    ]
}
```

### Documentation
- OpenAPI Yaml: (http://localhost:8081/openapi.yaml)
- Swagger: (http://localhost:8081/swagger.html)
- JavaDocs: (http://localhost:8081/index.html)
