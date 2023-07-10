FROM openjdk:latest
COPY target/BackendGlobalDirectory-0.0.1-SNAPSHOT.jar /app/BackendGlobalDirectory.jar
CMD ["java", "-jar", "/app/BackendGlobalDirectory.jar"]