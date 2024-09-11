FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY . .

EXPOSE 8080

RUN ./gradlew bootJar

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "./build/libs/cloud_file_storage-0.0.1-SNAPSHOT.jar"]