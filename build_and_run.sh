#!/bin/bash
docker build -t stock-market-app .

docker compose up -d

I=0
MAX=300

while [ "$(docker inspect --format='{{.State.Health.Status}}' simplified-stock-market-db-1)" != "healthy" ]; do
    if [ $I -ge $MAX ]; then
        echo "Database Fail to run in time ${MAX}s — exit"
        exit 1
    fi
    sleep 1
    I=$((I + 1))
done


for PORT in "$@"; do
    docker run \
      -p $PORT:8080 \
      --network="simplified-stock-market_default" \
      -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/stockmarket \
      -e SPRING_DATASOURCE_USERNAME=stockuser \
      -e SPRING_DATASOURCE_PASSWORD=stockpass \
      -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
      -e SERVER_PORT=8080 \
      -d \
      stock-market-app
done