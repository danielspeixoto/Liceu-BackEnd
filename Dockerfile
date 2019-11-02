FROM gradle:jdk12
COPY --chown=gradle:gradle . /home/gradle/src
COPY elastic-apm-agent-1.10.0.jar /elastic-apm-agent.jar
WORKDIR /home/gradle/src
RUN ls
RUN gradle build --info -x test
RUN cp /home/gradle/src/build/libs/*.jar /app.jar
ENV PORT 8080
CMD java -javaagent:/elastic-apm-agent.jar \
          -Delastic.apm.service_name=kotlinserver-${SERVICE_TYPE} \
          -Delastic.apm.server_url=${APM_SERVER} \
          -Delastic.apm.secret_token=${APM_SECRET_TOKEN} \
          -Delastic.apm.application_packages=com.liceu.server \
          -jar /app.jar
EXPOSE $PORT

