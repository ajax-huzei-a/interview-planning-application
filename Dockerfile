# syntax=docker/dockerfile:1

FROM amazoncorretto:11-alpine3.18

ENV APP_DIR=/app

RUN mkdir $APP_DIR
WORKDIR $APP_DIR

COPY build/libs/interview-planning-*.jar $APP_DIR/interview-planning.jar

CMD ["java", "-jar", "interview-planning.jar"]
