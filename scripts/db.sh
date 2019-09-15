#!/usr/bin/env bash

sudo docker rm --force -v kotlinServerDB
sudo docker run -d -p 27017:27017 --name kotlinServerDB mongo