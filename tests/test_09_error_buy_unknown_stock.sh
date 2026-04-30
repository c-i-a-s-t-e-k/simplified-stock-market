#!/usr/bin/env bash
# Test: buy nieistniejącego stocka → 404
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

echo "=== TEST 09: 404 — buy nieistniejącego stocka ==="
../stop.sh && ../start.sh
wait_for_app

CODE=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST "http://$HOST/wallets/wallet1/stocks/NONEXISTENT" \
  -H "Content-Type: application/json" -d '{"type": "buy"}')

assert_eq "404" "$CODE" "buy NONEXISTENT → HTTP 404"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
