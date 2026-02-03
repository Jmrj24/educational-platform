FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests
FROM openjdk-17-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app_educational-platform.jar.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app_educational-platform.jar.jar"]