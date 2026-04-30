#!/usr/bin/env bash
# Test: sell gdy portfel ma 0 akcji → 400
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

echo "=== TEST 08: 400 — sell gdy portfel ma 0 akcji ==="
../stop.sh && ../start.sh
wait_for_app

curl -s -o /dev/null -X POST "http://$HOST/stocks" \
  -H "Content-Type: application/json" -d '{"stocks": [{"name": "AAPL", "quantity": 5}]}'

CODE=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "http://$HOST/wallets/wallet1/stocks/AAPL" \
  -H "Content-Type: application/json" -d '{"type": "sell"}')

assert_eq "400" "$CODE" "sell z pustego portfela → HTTP 400"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
