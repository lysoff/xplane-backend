# xplane-backend
Backend application for XPlane

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

### How to run integration and unit tests
```shell
./gradlew test
```