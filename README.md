# Kotlin Order Book

This application is created for purpose of learning Kotlin and getting familiar with the Java/Kotlin ecosystem. It is a simple/dummy order book with price-time order matching algorithm. The communication between the server and the clients is achieved using WebSockets.

## Libraries used
- [Ktor](https://github.com/ktorio/ktor) - Framework
- [Koin](https://github.com/InsertKoinIO/koin) - Dependency Injection
- [HikariCP](https://github.com/brettwooldridge/HikariCP), [Exposed](https://github.com/JetBrains/Exposed), [Flyway](https://github.com/flyway/flyway) - DB layer and migration
- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) - JSON serialization 
- [JUnit 5](https://github.com/junit-team/junit5), [MockK](https://github.com/mockk/mockk),  [AssertJ](https://github.com/assertj/assertj-core), [JSONassert](https://github.com/skyscreamer/JSONassert) - Unit testing, mocking/stubbing, test assertions
- [Testcontainers](https://github.com/testcontainers/testcontainers-java) - Integration tests with a real db (MySQL)

## Run the app
```shell
$ git clone https://github.com/nenadvasic/order-book-example
$ cd order-book-example
$ docker-compose up --build
```
Docker will spin up two containers - one for the database and the second for the app. 
A simple HTML/JS page is also included and it is accessible at http://localhost:8080/

### TODO
- Move db integration tests to a separate test directory
- Explore more about dockerizing Java/Kotlin applications, coroutines, data validation, logging, ...
