# syntax=docker/dockerfile:1

FROM eclipse-temurin:11
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

COPY checkstyle.xml .

RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]