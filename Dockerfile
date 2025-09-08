# Dockerfile
FROM openjdk:17-jdk-slim
ARG APP_VERSION=v1
ENV APP_VERSION=${APP_VERSION}
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar","--app.version=${APP_VERSION}"]
