FROM eclipse-temurin:17-jdk
ARG JAR_FILE=target/educational-platform-0.0.1.jar
COPY ${JAR_FILE} app_educational-platform.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app_educational-platform.jar"]