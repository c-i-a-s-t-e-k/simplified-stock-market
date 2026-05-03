# stop.ps1
# Zatrzymuje uruchomione kontenery

Write-Host "Zatrzymywanie kontenerów przez docker compose..." -ForegroundColor Cyan
docker compose stop

# Zatrzymanie kontenerów stock-market-app uruchomionych poza compose
$runningContainers = docker ps -q --filter "ancestor=stock-market-app"

if ($runningContainers) {
    Write-Host "Zatrzymywanie dodatkowych kontenerów stock-market-app..." -ForegroundColor Cyan
    foreach ($containerId in $runningContainers) {
        docker stop $containerId
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Kontener $containerId zatrzymany." -ForegroundColor Green
        } else {
            Write-Host "Błąd przy zatrzymywaniu kontenera $containerId." -ForegroundColor Red
        }
    }
} else {
    Write-Host "Brak uruchomionych kontenerów stock-market-app." -ForegroundColor Yellow
}
