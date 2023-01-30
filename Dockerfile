# Use an official openjdk image as the base image
FROM gradle:7.6.0-jdk17 as builder

# Set the working directory
WORKDIR /app

# Copy the project files into the container
COPY . .

# Build the application
RUN gradle build --no-daemon

# Use an official openjdk image as the runtime image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy only the built jar from the previous stage
COPY --from=builder /app/build/libs/backendforfrontend-0.0.1-SNAPSHOT.jar .

# Run the application
CMD ["java", "-jar", "backendforfrontend-0.0.1-SNAPSHOT.jar"]

