# start.ps1
# Starts stopped containers

Write-Host "Starting containers via docker compose..." -ForegroundColor Cyan
docker compose start

# Start stopped stock-market-app containers
$stoppedContainers = docker ps -a -q --filter "status=exited" --filter "ancestor=stock-market-app"

if ($stoppedContainers) {
    Write-Host "Starting stopped stock-market-app containers..." -ForegroundColor Cyan
    foreach ($containerId in $stoppedContainers) {
        docker start $containerId
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Container $containerId started." -ForegroundColor Green
        } else {
            Write-Host "Error: failed to start container $containerId." -ForegroundColor Red
        }
    }
} else {
    Write-Host "No stopped stock-market-app containers found." -ForegroundColor Yellow
}
