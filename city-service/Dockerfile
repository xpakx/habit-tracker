# Build stage
FROM maven:3.9-eclipse-temurin-25-noble  AS build
WORKDIR /app
COPY . /app
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:25-noble
MAINTAINER xpakx.github.io
EXPOSE 8082
COPY --from=build app/target/habitcity-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java","-jar","/application.jar"]
