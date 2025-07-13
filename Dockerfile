#Start with a base image containing Java runtime
FROM openjdk:17
#Add Maintainer Info
LABEL maintainer="Salih Ağralı <salihagrali@outlook.com>"
#The application's JAR file
ARG JAR_FILE
#Add the application's JAR to the container
COPY ${JAR_FILE} app.jar
#execute the application
ENTRYPOINT ["java","-jar","/app.jar"]