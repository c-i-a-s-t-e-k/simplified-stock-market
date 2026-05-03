# start.ps1
# Uruchamia zatrzymane kontenery

Write-Host "Uruchamianie kontenerów przez docker compose..." -ForegroundColor Cyan
docker compose start

# Uruchomienie zatrzymanych kontenerów stock-market-app
$stoppedContainers = docker ps -a -q --filter "status=exited" --filter "ancestor=stock-market-app"

if ($stoppedContainers) {
    Write-Host "Uruchamianie zatrzymanych kontenerów stock-market-app..." -ForegroundColor Cyan
    foreach ($containerId in $stoppedContainers) {
        docker start $containerId
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Kontener $containerId uruchomiony." -ForegroundColor Green
        } else {
            Write-Host "Błąd przy uruchamianiu kontenera $containerId." -ForegroundColor Red
        }
    }
} else {
    Write-Host "Brak zatrzymanych kontenerów stock-market-app." -ForegroundColor Yellow
}
