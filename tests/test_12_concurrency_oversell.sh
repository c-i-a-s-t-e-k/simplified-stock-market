#!/usr/bin/env bash
# Test: 150 równoległych buy przy banku=100 — dokładnie 100 sukces, 50 odmów, brak 500
# Użycie: ./test_12_concurrency_oversell.sh [host1] [host2] [host3]
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

echo "=== TEST 12: Współbieżność — brak oversell (150 buy, bank=100) ==="
../stop.sh && ../start.sh
wait_for_app "$HOST1"; wait_for_app "$HOST2"; wait_for_app "$HOST3"

curl -s -o /dev/null -X POST "http://$HOST1/stocks" \
  -H "Content-Type: application/json" -d '{"stocks": [{"name": "AAPL", "quantity": 100}]}'

RESULTS=$(seq 1 150 | H1="$HOST1" H2="$HOST2" H3="$HOST3" xargs -P 150 -I{} bash -c '
  IDX=$(( ({} - 1) % 3 ))
  case $IDX in 0) HOST="$H1";; 1) HOST="$H2";; 2) HOST="$H3";; esac
  curl -s -o /dev/null -w "%{http_code}\n" \
    -X POST "http://$HOST/wallets/w{}/stocks/AAPL" \
    -H "Content-Type: application/json" -d "{\"type\": \"buy\"}"
')

OK=$(echo "$RESULTS"   | grep -c '^200$')
ERR400=$(echo "$RESULTS" | grep -c '^400$')
ERR500=$(echo "$RESULTS" | grep -c '^500$')
BANK=$(curl -s "http://$HOST1/stocks" | python3 -c "import sys,json; d=json.load(sys.stdin); print(next(s['quantity'] for s in d['stocks'] if s['name']=='AAPL'))")

assert_eq "100" "$OK"    "dokładnie 100 sukcesów (HTTP 200)"
assert_eq "50"  "$ERR400" "dokładnie 50 odmów (HTTP 400)"
assert_eq "0"   "$ERR500" "zero błędów serwera (HTTP 500)"
assert_eq "0"   "$BANK"   "bank AAPL = 0 po wyczerpaniu"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
