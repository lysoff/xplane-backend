# xplne-backend
Backend application for Xplne

### How to run locally
- make sure that Java 17 and Docker are installed
- execute this command if you are using Linux or macOS:
```shell
./gradlew run
```
- ... or this one for Windows:
```shell
.\gradlew run
```
Gradle will download all required dependencies and run a Spring Boot application. 
When the application starts, it automatically runs Postgres database in Docker using [compose.yaml](compose.yaml)

### REST API specification
When application is running, it has Swagger UI available on endpoint:
- http://localhost:8080/api/swagger-ui/index.html)
Also, it allows to download an OpenAPI 3.0 specification:
- in Json format: http://localhost:8080/api/api-docs/API-v1
- in YAML: http://localhost:8080/api/api-docs.yaml/API-v1

### How to run integration and unit tests
```shell
./gradlew test
```