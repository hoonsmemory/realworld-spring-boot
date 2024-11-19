# ![RealWorld Example App](logo.png)

> ### Spring Boot codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.


### [Demo](https://demo.realworld.io/)&nbsp;&nbsp;&nbsp;&nbsp;[RealWorld](https://github.com/gothinkster/realworld)


This codebase was created to demonstrate a fully fledged fullstack application built with **Java 17 + Spring Boot 3 + JPA** including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the **Java 17 + Spring Boot 3 + JPA** community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.


# How it works

The RealWorld project aims to create mini-blog applications with the same specifications using various technology stacks, allowing for a comparison between them.

This application provides the following key features:

1. User registration/login/logout
2. Article creation/viewing/editing/deletion
3. Article list viewing (with pagination, filtering, and sorting)
4. Comment creation/viewing/editing/deletion on articles
5. User profile viewing/editing

The project is implemented based on Java 17 and Spring Boot 3, utilizing various Spring technologies such as Spring MVC, Spring Data JPA, and Spring Security. It uses H2 DB (in-memory, MySQL mode) as the database and JUnit5 for writing test codes.

To run the project, JDK 17 must be installed first. Then, execute the ./gradlew bootRun command in the project root directory to run the application. After that, you can use the application by accessing http://localhost:8080 in your browser.

Taking a closer look at the project structure, the main code of the application is located in the src/main/java directory, while the test code is located in the src/test/java directory. Additionally, configuration files and such can be found in the
src/main/resources directory.

The core logic of the application is organized as follows:

- ~Controller: Processes HTTP requests, calls business logic, and generates responses.
- ~Service: Implements business logic and interacts with the database through Repositories.
- ~Repository: An interface for interacting with the database, implemented using Spring Data JPA.

Authentication and authorization management are implemented using Spring Security, with token-based authentication using JWT. Moreover, various features of Spring Boot are utilized to implement exception handling, logging, testing, and more.

Through this project, you can learn how to implement backend applications based on Spring and how to utilize various Spring technologies. Additionally, by implementing an application following the RealWorld specifications, it provides a basis for
deciding which technology stack to choose through comparisons with various other technology stacks.

# Database Architecture

> **Note:** I paid attention to data types, but did not pay much attention to size.

- [schema.sql](database/schema.sql)

# Getting started

> **Note:** You just need to have JDK 17 installed.
>
> **Note:** If permission denied occurs when running the gradle task, enter `chmod +x gradlew` to grant the permission.

## Run application

```shell
./gradlew bootRun
```

## Run test

> **Note:** Running this task will generate a test coverage report at `build/jacoco/html/index.html`.

```shell
./gradlew test
```

