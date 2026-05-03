# remove.ps1
# Usuwa kontenery i zasoby docker compose

Write-Host "Usuwanie kontenerów przez docker compose..." -ForegroundColor Cyan
docker compose down

# Usunięcie wszystkich kontenerów stock-market-app (również zatrzymanych)
$allContainers = docker ps -q -a --filter "ancestor=stock-market-app"

if ($allContainers) {
    Write-Host "Usuwanie kontenerów stock-market-app..." -ForegroundColor Cyan
    foreach ($containerId in $allContainers) {
        docker rm --force $containerId
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Kontener $containerId usunięty." -ForegroundColor Green
        } else {
            Write-Host "Błąd przy usuwaniu kontenera $containerId." -ForegroundColor Red
        }
    }
} else {
    Write-Host "Brak kontenerów stock-market-app do usunięcia." -ForegroundColor Yellow
}
