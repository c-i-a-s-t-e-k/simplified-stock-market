#!/usr/bin/env bash
# Test: POST /chaos zabija instancję, pozostałe żyją, dane nienaruszone
# Użycie: ./test_14_chaos.sh [host1] [host2] [host3]
HOST1=${1:-localhost:8081}
HOST2=${2:-localhost:8082}
HOST3=${3:-localhost:8083}
FAILED=0

assert_eq() { [ "$1" = "$2" ] && echo "  PASS: $3" || { echo "  FAIL: $3 (expected: '$1', got: '$2')"; FAILED=1; }; }

wait_for_app() {
  local host=$1 retries=30
  until curl -s -o /dev/null -w "%{http_code}" --connect-timeout 1 "http://$host/stocks" | grep -q "200"; do
    sleep 1; retries=$(( retries - 1 ))
    [ $retries -le 0 ] && echo "BŁĄD: app nie odpowiada na $host" && exit 1
  done
}

echo "=== TEST 14: POST /chaos — HA i trwałość danych ==="
../stop.sh && ../start.sh
wait_for_app "$HOST1"; wait_for_app "$HOST2"; wait_for_app "$HOST3"

# Setup: dane w bazie
curl -s -o /dev/null -X POST "http://$HOST1/stocks" \
  -H "Content-Type: application/json" \
  -d '{"stocks": [{"name": "AAPL", "quantity": 10}, {"name": "GOOG", "quantity": 5}]}'
for i in 1 2 3; do
  curl -s -o /dev/null -X POST "http://$HOST1/wallets/wallet$i/stocks/AAPL" \
    -H "Content-Type: application/json" -d '{"type": "buy"}'
done

BANK_BEFORE=$(curl -s "http://$HOST1/stocks")
LOG_BEFORE=$(curl -s "http://$HOST1/log" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d['log']))")
echo "  Setup: bank=$BANK_BEFORE, log=$LOG_BEFORE wpisów"

# Wszystkie instancje żywe przed chaos
for HOST in "$HOST1" "$HOST2" "$HOST3"; do
  CODE=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 2 "http://$HOST/stocks")
  assert_eq "200" "$CODE" "$HOST żywy przed chaos"
done

# Chaos na HOST1
CHAOS_CODE=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 5 -X POST "http://$HOST1/chaos")
assert_eq "200" "$CHAOS_CODE" "POST /chaos → HTTP 200"
sleep 1

# HOST1 martwy
CODE1=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 2 "http://$HOST1/stocks")
assert_eq "000" "$CODE1" "$HOST1 martwy po chaos"

# HOST2 i HOST3 żywe
for HOST in "$HOST2" "$HOST3"; do
  CODE=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 2 "http://$HOST/stocks")
  assert_eq "200" "$CODE" "$HOST nadal żywy po chaos"
done

# Dane nienaruszone
BANK_AFTER=$(curl -s "http://$HOST2/stocks")
LOG_AFTER=$(curl -s "http://$HOST2/log" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d['log']))")
WALLET_QTY=$(curl -s "http://$HOST3/wallets/wallet1/stocks/AAPL")

assert_eq "$BANK_BEFORE" "$BANK_AFTER" "bank niezmieniony na $HOST2"
assert_eq "$LOG_BEFORE"  "$LOG_AFTER"  "log niezmieniony na $HOST2 ($LOG_AFTER wpisów)"
assert_eq "1"            "$WALLET_QTY" "portfel wallet1 niezmieniony na $HOST3"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
