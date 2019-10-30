FROM gradle:jdk12
COPY --chown=gradle:gradle . /home/gradle/src
COPY elastic-apm-agent-1.10.0.jar /elastic-apm-agent.jar
WORKDIR /home/gradle/src
RUN ls
RUN gradle build --info -x test
RUN cp /home/gradle/src/build/libs/*.jar /app.jar
ENV PORT 8080
CMD java -javaagent:/elastic-apm-agent.jar \
          -Delastic.apm.service_name=kotlinserver \
          -Delastic.apm.server_url=https://a050228acf444f38b2c20e2313c9df8b.apm.us-east-1.aws.cloud.es.io:443 \
          -Delastic.apm.secret_token=uYrJ1jrJAOMApGlGFV \
          -Delastic.apm.application_packages=com.liceu.server \
          -jar /app.jar
EXPOSE $PORT

