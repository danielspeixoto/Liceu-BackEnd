#!/usr/bin/env bash

export MONGO_CONNECTION=mongodb://localhost:27017
export DB_NAME=test

./gradlew test --tests "com.liceu.server.integration.**" --info && \
./gradlew test --tests "com.liceu.server.system.**" --info