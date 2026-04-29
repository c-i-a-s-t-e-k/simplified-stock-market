#!/bin/bash

docker compose stop
docker stop $(docker ps -q --filter "ancestor=stock-market-app")