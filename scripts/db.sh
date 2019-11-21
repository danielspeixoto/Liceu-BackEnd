#!/usr/bin/env bash

sudo docker rm --force -v kotlinServerDB
sudo docker run -d -p 27017:27017 --name kotlinServerDB mongo
sudo docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.4.2
