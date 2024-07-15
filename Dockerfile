FROM maven:3.9.5 AS build
WORKDIR /app
COPY pom.xml .
COPY ./.mvn/wrapper/settings.xml /root/.m2/settings.xml
RUN mvn dependency:go-offline
COPY keystore.p12 . 
COPY src ./src
RUN mvn clean package -DskipTests

FROM ubuntu:22.04
WORKDIR /app

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/keystore.p12 keystore.p12

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]