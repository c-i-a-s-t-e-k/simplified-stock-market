# remove.ps1
# Removes containers and docker compose resources

Write-Host "Removing containers via docker compose..." -ForegroundColor Cyan
docker compose down

# Remove all stock-market-app containers (including stopped ones)
$allContainers = docker ps -q -a --filter "ancestor=stock-market-app"

if ($allContainers) {
    Write-Host "Removing stock-market-app containers..." -ForegroundColor Cyan
    foreach ($containerId in $allContainers) {
        docker rm --force $containerId
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Container $containerId removed." -ForegroundColor Green
        } else {
            Write-Host "Error: failed to remove container $containerId." -ForegroundColor Red
        }
    }
} else {
    Write-Host "No stock-market-app containers to remove." -ForegroundColor Yellow
}
