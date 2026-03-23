# Backend Dockerfile
FROM maven:3.9-eclipse-temurin-21 as builder

WORKDIR /app

# Copy pom
COPY backend/accounting-service/pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source
COPY backend/accounting-service/src src/

# Build
RUN mvn clean package -DskipTests

# Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
