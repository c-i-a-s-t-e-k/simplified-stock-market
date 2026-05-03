# build_and_run.ps1
# Builds Docker image and starts application containers
# Usage: .\build_and_run.ps1 8081 8082 8083

param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$Ports
)

# Build image
Write-Host "Building stock-market-app image..." -ForegroundColor Cyan
docker build -t stock-market-app .
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: image build failed!" -ForegroundColor Red
    exit 1
}

# Start docker compose
Write-Host "Starting docker compose..." -ForegroundColor Cyan
docker compose up -d
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: docker compose failed!" -ForegroundColor Red
    exit 1
}

# Wait for database to be healthy
Write-Host "Waiting for database to be ready..." -ForegroundColor Cyan
$I = 0
$MAX = 300

while ($true) {
    $status = docker inspect --format='{{.State.Health.Status}}' simplified-stock-market-db-1 2>$null
    if ($status -eq "healthy") {
        Write-Host "Database is ready!" -ForegroundColor Green
        break
    }
    if ($I -ge $MAX) {
        Write-Host "Error: database did not start within ${MAX}s -- exiting" -ForegroundColor Red
        exit 1
    }
    Start-Sleep -Seconds 1
    $I++
}

# Start application containers on given ports
if ($Ports.Count -eq 0) {
    Write-Host "No ports provided. Usage: .\build_and_run.ps1 8081 8082 ..." -ForegroundColor Yellow
    exit 0
}

foreach ($Port in $Ports) {
    Write-Host "Starting container on port $Port..." -ForegroundColor Cyan
    docker run `
        -p "${Port}:8080" `
        --network="simplified-stock-market_default" `
        -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/stockmarket `
        -e SPRING_DATASOURCE_USERNAME=stockuser `
        -e SPRING_DATASOURCE_PASSWORD=stockpass `
        -e SPRING_JPA_HIBERNATE_DDL_AUTO=update `
        -e SERVER_PORT=8080 `
        -d `
        stock-market-app
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: failed to start container on port $Port!" -ForegroundColor Red
    } else {
        Write-Host "Container on port $Port started successfully." -ForegroundColor Green
    }
}
