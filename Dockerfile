FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim
COPY build/libs/se09-user-service-*-all.jar se09-user-service.jar
EXPOSE 8181
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar se09-user-service.jar