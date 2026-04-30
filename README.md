# simplified-stock-market

The project implements the task described in [doc/task_for_intership_2026.md](doc/task_for_intership_2026.md). The main goal is to implement a concurrency-resilient system with a bank and stocks that can be bought by wallets, with simple liquidity flow.

If you need more technical information about the project, see [TECHNICAL.md](TECHNICAL.md).

## Setup and start

To run the project you will need Docker and an available internet connection to download all required Java dependencies and the PostgreSQL database Docker image.

To build and run the app:
```bash
chmod +x ./build_and_run.sh
./build_and_run.sh 8081 8082 8083 # number of ports — 1 port = 1 app instance
```

If already built, you can get a fresh environment with the following commands. Note: these commands work only on containers created with the build script. If you want to set up a different number of instances, you need to remove the current environment first and then build again.
```bash
chmod +x ./start.sh
chmod +x ./stop.sh

./stop.sh
./start.sh
```

To remove containers and clean up:
```bash
chmod +x ./remove.sh
./remove.sh
```

## REST Server

The app exposes API endpoints as specified in [doc/task_for_intership_2026.md](doc/task_for_intership_2026.md). Additionally, OpenAPI documentation is available at:

```
/swagger-ui/index.html  # human-readable API endpoint docs
/v3/api-docs            # JSON containing all available endpoints
/v3/api-docs.yaml       # same as above in YAML format
```

For more details on error responses for each endpoint, see [TECHNICAL.md](TECHNICAL.md).