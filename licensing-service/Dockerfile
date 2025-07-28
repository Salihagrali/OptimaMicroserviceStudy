#stage 1
FROM openjdk:17 AS build
LABEL maintainer="Salih Ağralı <salihagrali@outlook.com>"
ARG JAR_FILE=target/licensing-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
RUN mkdir -p target/dependency && \
    (cd target/dependency; \
    jar -xf /app.jar)

#stage 2
FROM openjdk:17
VOLUME /tmp
ARG DEPENDENCY=/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.myproject.microservices.licensingservice.LicensingServiceApplication"]

#docker run ostock/licensing-service:0.0.1-SNAPSHOT

#-d option runs the container in the background
#docker run -d ostock/licensing-service:0.0.1-SNAPSHOT

#docker ps to see all the running containers
#docker stop <container_id> to stop a running container