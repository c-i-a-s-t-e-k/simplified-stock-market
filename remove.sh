#!/bin/bash

docker compose down
docker rm --force $(docker ps -q -a --filter "ancestor=stock-market-app")