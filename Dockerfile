FROM maven:latest AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn package -DskipTests -X

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/Robo-Arena-1.0-SNAPSHOT-webapi-jar-with-dependencies.jar ./robo-arena-webapi.jar
COPY --from=build /app/src/main/resources/static ./src/main/resources/static

RUN apt-get update && apt-get install -y sqlite3

EXPOSE 7000

ENTRYPOINT ["java", "-jar", "robo-arena-webapi.jar"]
