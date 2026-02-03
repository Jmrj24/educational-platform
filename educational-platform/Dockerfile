FROM maven:3.8.5-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app_educational-platform.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app_educational-platform.jar"]