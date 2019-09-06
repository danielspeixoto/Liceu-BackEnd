FROM gradle:jdk12
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ls
RUN gradle build --info -x test
RUN cp /home/gradle/src/build/libs/*.jar /app.jar
ENV PORT 8080
CMD java -jar /app.jar
EXPOSE $PORT