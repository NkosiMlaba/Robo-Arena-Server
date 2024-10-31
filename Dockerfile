FROM maven:latest AS build

WORKDIR /app

COPY target/Robo-Arena-1.0-SNAPSHOT-webapi-jar-with-dependencies.jar /app/robo-arena-webapi.jar

RUN apt-get update && apt-get install -y sqlite3

EXPOSE 5050

ENTRYPOINT ["java", "-jar", "robo-arena-webapi.jar"]

