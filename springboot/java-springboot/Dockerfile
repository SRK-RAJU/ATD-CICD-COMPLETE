# Build stage
FROM maven:latest
WORKDIR /workspace
COPY .. pom.xml /workspace 
COPY ..  src /workspace/src 
RUN mvn -f pom.xml clean test package

# Package stage
FROM openjdk:8-jdk-alpine
COPY  target/*.jar /app.jar
EXPOSE 8080
CMD ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
