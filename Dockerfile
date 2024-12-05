FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the packaged Spring Boot JAR file into the container
COPY target/codingassignment-1.0.jar /app/codingassignment.jar

# Expose the required ports
EXPOSE 8080
EXPOSE 5005

# Run the Spring Boot application with debugging enabled
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app/codingassignment.jar"]
