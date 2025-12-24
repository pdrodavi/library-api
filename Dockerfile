FROM maven:3.8.8-openjdk-15 AS builder
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
COPY src ./src
RUN ./mvnw -B -DskipTests package
FROM openjdk:15-jdk-slim
ARG JAR_FILE=target/.jar
COPY --from=builder /app/target/.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]