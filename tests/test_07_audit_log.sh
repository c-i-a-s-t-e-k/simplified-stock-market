#!/usr/bin/env bash
# Test: GET /log — kolejność wpisów, tylko udane operacje
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

echo "=== TEST 07: GET /log — audit log ==="
../stop.sh && ../start.sh
wait_for_app

curl -s -o /dev/null -X POST "http://$HOST/stocks" \
  -H "Content-Type: application/json" -d '{"stocks": [{"name": "AAPL", "quantity": 5}]}'

# buy → sell (udane), potem nieudany sell — nie powinien trafić do logu
curl -s -o /dev/null -X POST "http://$HOST/wallets/$WALLET/stocks/AAPL" \
  -H "Content-Type: application/json" -d '{"type": "buy"}'
curl -s -o /dev/null -X POST "http://$HOST/wallets/$WALLET/stocks/AAPL" \
  -H "Content-Type: application/json" -d '{"type": "sell"}'
curl -s -o /dev/null -X POST "http://$HOST/wallets/$WALLET/stocks/AAPL" \
  -H "Content-Type: application/json" -d '{"type": "sell"}'

CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://$HOST/log")
BODY=$(curl -s "http://$HOST/log")

assert_eq "200" "$CODE" "GET /log HTTP 200"

LOG_LEN=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d['log']))")
assert_eq "2" "$LOG_LEN" "log ma dokładnie 2 wpisy (buy + sell, bez nieudanej operacji)"

FIRST_TYPE=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d['log'][0]['type'])")
SECOND_TYPE=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d['log'][1]['type'])")
WALLET_ID=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d['log'][0]['wallet_id'])")

assert_eq "buy"     "$FIRST_TYPE"  "pierwszy wpis: buy"
assert_eq "sell"    "$SECOND_TYPE" "drugi wpis: sell"
assert_eq "$WALLET" "$WALLET_ID"   "wallet_id zgodny"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
