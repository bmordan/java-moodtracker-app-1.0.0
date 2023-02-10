FROM openjdk:17-jdk-slim

RUN mkdir /app
WORKDIR /app
COPY ./build/libs/*.jar .
RUN chmod +x *.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/moodtracker-0.0.1-SNAPSHOT.jar"]