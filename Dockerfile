FROM openjdk:8-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} bank-accounts.jar

ENTRYPOINT ["java","-jar","/bank-accounts.jar"]