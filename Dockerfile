# Use an official openjdk image as the base image
FROM openjdk:17-jdk

# Set the working directory
WORKDIR /app

# Copy the application jar file to the container
COPY target/*.jar app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "app.jar"]
