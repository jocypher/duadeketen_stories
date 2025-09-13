# 1. Start from an official OpenJDK 17 image
FROM openjdk:17-jdk-slim

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy Maven wrapper and pom.xml first for caching dependencies
COPY mvnw pom.xml ./
COPY .mvn .mvn

# 4. Make mvnw executable
RUN chmod +x mvnw

# 5. Download all dependencies (speeds up rebuilds)
RUN ./mvnw dependency:go-offline

# 6. Copy the source code
COPY src ./src

# 7. Build the app (skip tests for speed)
RUN ./mvnw clean package -DskipTests

# 8. Expose the port Spring Boot will run on
EXPOSE 8080

# 9. Set the default command to run the app
CMD ["java", "-jar", "target/duade-tts-0.0.1-SNAPSHOT.jar"]
