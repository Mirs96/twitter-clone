# Use a slim OpenJDK image with Java 23
FROM openjdk:23-slim

# Set the working directory inside the container
WORKDIR /app

COPY seed-images/ /app/seed-images/

ARG JAR_FILE=target/backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
