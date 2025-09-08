# Multi-stage build for Spring Boot (Maven)
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Cache dependencies first
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -q dependency:go-offline

# Copy sources and build (skip tests in image build; run tests in CI)
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Expose Spring Boot default port
EXPOSE 8080

# Copy the fat jar from the build stage
# If your artifactId/version differs, override with build args or adjust the path below
COPY --from=build /workspace/target/*.jar /app/app.jar

# Use a non-root user where possible
RUN useradd -ms /bin/bash appuser && chown -R appuser:appuser /app
USER appuser

# Health and JVM tuning can be passed via JAVA_OPTS
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
