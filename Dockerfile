FROM openjdk:11-jdk-slim

EXPOSE 8080

ARG JAR_FILE=target/SimpleMqttPublisher-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} simplemqtt.jar

ENTRYPOINT ["java","-jar","/simplemqtt.jar"]
