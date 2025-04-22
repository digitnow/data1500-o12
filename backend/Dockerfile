# Bygg-trinn
FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package assembly:single

# Kjør-trinn
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/*-jar-with-dependencies.jar ./app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]