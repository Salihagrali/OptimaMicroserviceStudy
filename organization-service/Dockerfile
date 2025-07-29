#stage 1
#Start with a base image containing Java JDK
FROM openjdk:17-jdk AS build

# Add Maintainer Info
LABEL maintainer="Salih Ağralı"

# The application's jar file
ARG JAR_FILE=target/organization-service-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

# Unpackage jar file
RUN mkdir -p target/dependency && \
              cd target/dependency && \
              jar -xf /app.jar

#stage 2
#Same Java runtime
FROM openjdk:17-jdk

# Add volume pointing to /tmp
VOLUME /tmp

# Copy unpackaged application to new container
ARG DEPENDENCY=/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Execute the application
ENTRYPOINT ["java","-cp","app:app/lib/*","com.optimagrowth.organizationservice.OrganizationServiceApplication"]