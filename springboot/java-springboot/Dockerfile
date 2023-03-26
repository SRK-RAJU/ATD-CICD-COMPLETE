# Build stage
FROM maven:latest AS build
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
RUN mvn -f pom.xml clean test package

# Package stage
FROM openjdk:8-alpine
COPY --from=build /workspace/target/*.jar /app.jar
EXPOSE 8080
CMD ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
