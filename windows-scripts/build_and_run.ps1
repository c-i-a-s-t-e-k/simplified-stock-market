# build_and_run.ps1
# Buduje obraz Docker i uruchamia kontenery aplikacji
# Użycie: .\build_and_run.ps1 8081 8082 8083

param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$Ports
)

Write-Host "Budowanie obrazu stock-market-app..." -ForegroundColor Cyan
docker build -t stock-market-app .
if ($LASTEXITCODE -ne 0) {
    Write-Host "Błąd podczas budowania obrazu!" -ForegroundColor Red
    exit 1
}

Write-Host "Uruchamianie docker compose..." -ForegroundColor Cyan
docker compose up -d
if ($LASTEXITCODE -ne 0) {
    Write-Host "Błąd podczas uruchamiania docker compose!" -ForegroundColor Red
    exit 1
}

Write-Host "Oczekiwanie na gotowość bazy danych..." -ForegroundColor Cyan
$I = 0
$MAX = 300

while ($true) {
    $status = docker inspect --format='{{.State.Health.Status}}' simplified-stock-market-db-1 2>$null
    if ($status -eq "healthy") {
        Write-Host "Baza danych jest gotowa!" -ForegroundColor Green
        break
    }
    if ($I -ge $MAX) {
        Write-Host "Baza danych nie uruchomiła się w czasie ${MAX}s — wyjście" -ForegroundColor Red
        exit 1
    }
    Start-Sleep -Seconds 1
    $I++
}

if ($Ports.Count -eq 0) {
    Write-Host "Nie podano żadnych portów. Użycie: .\build_and_run.ps1 8081 8082 ..." -ForegroundColor Yellow
    exit 0
}

foreach ($Port in $Ports) {
    Write-Host "Uruchamianie kontenera na porcie $Port..." -ForegroundColor Cyan
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
        Write-Host "Błąd podczas uruchamiania kontenera na porcie $Port!" -ForegroundColor Red
    } else {
        Write-Host "Kontener na porcie $Port uruchomiony pomyślnie." -ForegroundColor Green
    }
}
