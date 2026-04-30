#!/bin/bash

docker compose down
docker rm --force $(docker ps -q --filter "ancestor=stock-market-app")