#!/usr/bin/env bash
# Test: GET /stocks zwraca pustą listę na świeżym środowisku
# Uruchamiaj z katalogu repozytorium: bash skrypty/test_01_bank_get_empty.sh
HOST=${1:-localhost:8081}
FAILED=0

assert_eq() { [ "$1" = "$2" ] && echo "  PASS: $3" || { echo "  FAIL: $3 (expected: '$1', got: '$2')"; FAILED=1; }; }

wait_for_app() {
  local retries=30
  until curl -s -o /dev/null -w "%{http_code}" --connect-timeout 1 "http://$HOST/stocks" | grep -q "200"; do
    sleep 1; retries=$(( retries - 1 ))
    [ $retries -le 0 ] && echo "BŁĄD: app nie odpowiada na $HOST" && exit 1
  done
}

echo "=== TEST 01: GET /stocks — pusty bank ==="
../stop.sh && ../start.sh
wait_for_app

CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://$HOST/stocks")
BODY=$(curl -s "http://$HOST/stocks")

assert_eq "200"           "$CODE" "HTTP 200"
assert_eq '{"stocks":[]}' "$BODY" "pusta lista stocks"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
