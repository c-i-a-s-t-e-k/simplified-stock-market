#!/usr/bin/env bash
# Test: buy — tworzy portfel, zmniejsza bank o 1
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

echo "=== TEST 03: POST buy ==="
../stop.sh && ../start.sh
wait_for_app

curl -s -o /dev/null -X POST "http://$HOST/stocks" \
  -H "Content-Type: application/json" -d '{"stocks": [{"name": "AAPL", "quantity": 10}]}'

BUY_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "http://$HOST/wallets/$WALLET/stocks/AAPL" \
  -H "Content-Type: application/json" -d '{"type": "buy"}')

assert_eq "200" "$BUY_CODE" "POST buy HTTP 200"

BANK_AAPL=$(curl -s "http://$HOST/stocks" | python3 -c "import sys,json; d=json.load(sys.stdin); print(next(s['quantity'] for s in d['stocks'] if s['name']=='AAPL'))")
WALLET_AAPL=$(curl -s "http://$HOST/wallets/$WALLET/stocks/AAPL")

assert_eq "9" "$BANK_AAPL"   "bank AAPL zmniejszony do 9"
assert_eq "1" "$WALLET_AAPL" "portfel ma 1 AAPL"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
