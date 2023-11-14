# syntax=docker/dockerfile:1

FROM amazoncorretto:17-alpine3.18

ENV APP_DIR=/app

RUN mkdir $APP_DIR
WORKDIR $APP_DIR

COPY app/build/libs/app-*.jar $APP_DIR/app.jar

CMD ["java", "-jar", "app.jar"]
