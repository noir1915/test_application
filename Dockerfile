FROM openjdk:17-jdk-slim as build
WORKDIR /app
COPY ./target/user_subscription_service-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]