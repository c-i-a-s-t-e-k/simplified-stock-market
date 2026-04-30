#!/usr/bin/env bash
# Test: buy gdy bank ma 0 sztuk → 400
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

echo "=== TEST 10: 400 — buy gdy bank ma 0 sztuk ==="
../stop.sh && ../start.sh
wait_for_app

curl -s -o /dev/null -X POST "http://$HOST/stocks" \
  -H "Content-Type: application/json" -d '{"stocks": [{"name": "AAPL", "quantity": 1}]}'
# wyczerpaj bank
curl -s -o /dev/null -X POST "http://$HOST/wallets/wallet1/stocks/AAPL" \
  -H "Content-Type: application/json" -d '{"type": "buy"}'

BANK_AAPL=$(curl -s "http://$HOST/stocks" | python3 -c "import sys,json; d=json.load(sys.stdin); print(next(s['quantity'] for s in d['stocks'] if s['name']=='AAPL'))")
assert_eq "0" "$BANK_AAPL" "bank AAPL wyczerpany = 0"

CODE=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "http://$HOST/wallets/wallet2/stocks/AAPL" \
  -H "Content-Type: application/json" -d '{"type": "buy"}')

assert_eq "400" "$CODE" "buy przy pustym banku → HTTP 400"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
