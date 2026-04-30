#!/usr/bin/env bash
# Test: GET /wallets/{id}/stocks/{name} — zwraca liczbę akcji
HOST=${1:-localhost:8081}
FAILED=0
WALLET="wallet1"

assert_eq() { [ "$1" = "$2" ] && echo "  PASS: $3" || { echo "  FAIL: $3 (expected: '$1', got: '$2')"; FAILED=1; }; }

wait_for_app() {
  local retries=30
  until curl -s -o /dev/null -w "%{http_code}" --connect-timeout 1 "http://$HOST/stocks" | grep -q "200"; do
    sleep 1; retries=$(( retries - 1 ))
    [ $retries -le 0 ] && echo "BŁĄD: app nie odpowiada na $HOST" && exit 1
  done
}

echo "=== TEST 06: GET /wallets/{id}/stocks/{name} ==="
../stop.sh && ../start.sh
wait_for_app

curl -s -o /dev/null -X POST "http://$HOST/stocks" \
  -H "Content-Type: application/json" -d '{"stocks": [{"name": "AAPL", "quantity": 5}]}'
for _ in 1 2 3; do
  curl -s -o /dev/null -X POST "http://$HOST/wallets/$WALLET/stocks/AAPL" \
    -H "Content-Type: application/json" -d '{"type": "buy"}'
done

CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://$HOST/wallets/$WALLET/stocks/AAPL")
QTY=$(curl -s "http://$HOST/wallets/$WALLET/stocks/AAPL")

assert_eq "200" "$CODE" "GET /wallets/.../stocks/AAPL HTTP 200"
assert_eq "3"   "$QTY"  "ilość AAPL w portfelu = 3"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
