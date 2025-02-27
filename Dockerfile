FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY build/libs/task_management.jar /app/service.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/service.jar"]