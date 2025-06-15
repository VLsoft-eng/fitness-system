FROM eclipse-temurin:21-jre
WORKDIR /app

COPY build/libs/fitness-system-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/db/migration /app/db/migration

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]