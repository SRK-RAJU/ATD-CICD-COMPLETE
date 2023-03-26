# Build stage
FROM maven:3.9.1-jdk-17-slim AS build
WORKDIR /home/app
COPY pom.xml /home/app
COPY src /home/app/src
RUN mvn -f pom.xml clean test package

# Package stage
FROM openjdk:latest
COPY --from=build /home/app/target/*.jar /app.jar
EXPOSE 8080
CMD ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
