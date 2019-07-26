FROM gradle:5.4.1-jdk8-alpine AS build
ENV API_KEY=apikey
ENV AUTH_PREFIX=12345678901234567890123456789012
ENV AUTH_SECRET=1234567890123456789012345678901212345678901234567890123456789012
ENV MONGO_CONNECTION=mongodb://10.0.2.15:27017
ENV MONGO_DB_NAME=test
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:8-jre-slim
ENV API_KEY=apikey
ENV AUTH_PREFIX=12345678901234567890123456789012
ENV AUTH_SECRET=1234567890123456789012345678901212345678901234567890123456789012
ENV MONGO_CONNECTION=mongodb://10.0.2.15:27017
ENV MONGO_DB_NAME=test
EXPOSE 8080RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]
