#!/usr/bin/env bash
# Test: GET /wallets/{id} — struktura i zawartość portfela
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

echo "=== TEST 05: GET /wallets/{id} ==="
../stop.sh && ../start.sh
wait_for_app

curl -s -o /dev/null -X POST "http://$HOST/stocks" \
  -H "Content-Type: application/json" -d '{"stocks": [{"name": "AAPL", "quantity": 5}, {"name": "GOOG", "quantity": 5}]}'
curl -s -o /dev/null -X POST "http://$HOST/wallets/$WALLET/stocks/AAPL" \
  -H "Content-Type: application/json" -d '{"type": "buy"}'
curl -s -o /dev/null -X POST "http://$HOST/wallets/$WALLET/stocks/GOOG" \
  -H "Content-Type: application/json" -d '{"type": "buy"}'

CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://$HOST/wallets/$WALLET")
BODY=$(curl -s "http://$HOST/wallets/$WALLET")

assert_eq "200" "$CODE" "GET /wallets HTTP 200"

ID=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d['id'])")
COUNT=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d['stocks']))")
AAPL=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(next(s['quantity'] for s in d['stocks'] if s['name']=='AAPL'))")
GOOG=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(next(s['quantity'] for s in d['stocks'] if s['name']=='GOOG'))")

assert_eq "$WALLET" "$ID"    "id portfela = $WALLET"
assert_eq "2"       "$COUNT" "portfel ma 2 różne stocki"
assert_eq "1"       "$AAPL"  "AAPL quantity = 1"
assert_eq "1"       "$GOOG"  "GOOG quantity = 1"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
