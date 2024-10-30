FROM maven:latest



WORKDIR /app

COPY target/RobotWorld-1.0-SNAPSHOT-server-jar-with-dependencies.jar /app/brownfields-server.jar

RUN mkdir -p /app/database

COPY database/worlds.db /app/database/worlds.db

RUN apt-get update && apt-get install -y sqlite3

EXPOSE 5050

ENTRYPOINT ["java", "-jar", "brownfields-server.jar", "-p", "5050"]

