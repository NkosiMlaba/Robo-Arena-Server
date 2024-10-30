FROM maven:latest

WORKDIR /app

COPY target/Robo-Arena-1.0-SNAPSHOT-webapi-jar-with-dependencies.jar /app/robo-arena-webapi.jar

RUN mkdir -p /app/database

COPY database/worlds.db /app/database/worlds.db

RUN apt-get update && apt-get install -y sqlite3

EXPOSE 5050

ENTRYPOINT ["java", "-jar", "robo-arena-webapi.jar", "-p", "5050"]

