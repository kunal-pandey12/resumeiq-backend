FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY ResumeIQ/pom.xml .
COPY ResumeIQ/src ./src
COPY ResumeIQ/.mvn ./.mvn
COPY ResumeIQ/mvnw .
COPY ResumeIQ/mvnw.cmd .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
