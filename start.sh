#!/bin/bash
docker compose start
docker start $(docker ps -a -q --filter "status=exited" --filter "ancestor=stock-market-app")
