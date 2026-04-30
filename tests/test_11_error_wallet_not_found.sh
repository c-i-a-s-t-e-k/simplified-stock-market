#!/usr/bin/env bash
# Test: GET /wallets/{id} dla nieistniejącego portfela → 404
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

echo "=== TEST 11: 404 — GET nieistniejącego portfela ==="
../stop.sh && ../start.sh
wait_for_app

CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://$HOST/wallets/nonexistent_wallet")

assert_eq "404" "$CODE" "GET nieistniejący portfel → HTTP 404"

[ $FAILED -eq 0 ] && echo "RESULT: PASS" || echo "RESULT: FAIL"
exit $FAILED
