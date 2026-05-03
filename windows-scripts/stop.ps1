# stop.ps1
# Stops running containers

Write-Host "Stopping containers via docker compose..." -ForegroundColor Cyan
docker compose stop

# Stop stock-market-app containers running outside compose
$runningContainers = docker ps -q --filter "ancestor=stock-market-app"

if ($runningContainers) {
    Write-Host "Stopping additional stock-market-app containers..." -ForegroundColor Cyan
    foreach ($containerId in $runningContainers) {
        docker stop $containerId
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Container $containerId stopped." -ForegroundColor Green
        } else {
            Write-Host "Error: failed to stop container $containerId." -ForegroundColor Red
        }
    }
} else {
    Write-Host "No running stock-market-app containers found." -ForegroundColor Yellow
}
