FROM openjdk:9.0.1-jre-slim
ARG JAR_FILE
ADD ${JAR_FILE} worksample.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/worksample.jar"]