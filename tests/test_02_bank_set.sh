#!/usr/bin/env bash
# Test: POST /stocks ustawia stan banku, GET /stocks zwraca go poprawnie
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

echo "=== TEST 02: POST /stocks — ustaw stan banku ==="
../stop.sh && ../start.sh
wait_for_app

POST_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "http://$HOST/stocks" \
  -H "Content-Type: application/json" \
  -d '{"stocks": [{"name": "AAPL", "quantity": 10}, {"name": "GOOG", "quantity": 5}]}')

assert_eq "200" "$POST_CODE" "POST /stocks HTTP 200"

BODY=$(curl -s "http://$HOST/stocks")
GET_CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://$HOST/stocks")
AAPL=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(next(s['quantity'] for s in d['stocks'] if s['name']=='AAPL'))")
GOOG=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(next(s['quantity'] for s in d['stocks'] if s['name']=='GOOG'))")

assert_eq "200" "$GET_CODE" "GET /stocks HTTP 200"
assert_eq "10"  "$AAPL"    "AAPL quantity = 10"
assert_eq "5"   "$GOOG"    "GOOG quantity = 5"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
