#!/usr/bin/env bash

docker rm --force -v kotlinServer
docker run -d -p 27017:27017 --name kotlinServer mongo

export MONGO_CONNECTION=mongodb://localhost:27017
export DB_NAME=test

./gradlew test --tests "com.liceu.server.integration.**" --info && \
./gradlew test --tests "com.liceu.server.system.**" --info