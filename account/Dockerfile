# build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . /app
RUN mvn clean package -DskipTests

# deploy
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar /account.jar
ENTRYPOINT ["java","-jar","/account.jar"]