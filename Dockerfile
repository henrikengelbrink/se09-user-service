#FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim
#COPY build/libs/se09-user-service-*-all.jar se09-user-service.jar
#EXPOSE 8181
#CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar se09-user-service.jar

FROM gradle:jdk8-alpine as build
COPY --chown=gradle:gradle ./ /home/gradle/
WORKDIR /home/gradle/
RUN gradle clean build -x test

FROM openjdk:8-jre-alpine as docker
COPY --from=build /home/gradle/build/libs/se09-user-service-*-all.jar se09-user-service.jar
EXPOSE 8181
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar se09-user-service.jar
