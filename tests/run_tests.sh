#!/usr/bin/env bash
# Uruchamia wszystkie testy dla Stock Market API.
# Każdy test restartuje środowisko przez ./stop.sh i ./start.sh.
#
# Użycie: ./run_tests.sh [host1] [host2] [host3]
# Domyślnie: localhost:8081 localhost:8082 localhost:8083
#
# Uruchamiaj z katalogu repozytorium (tam gdzie są stop.sh i start.sh).

HOST1=${1:-localhost:8081}
HOST2=${2:-localhost:8082}
HOST3=${3:-localhost:8083}

PASS=0
FAIL=0

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

run_test() {
  local script=$1; shift
  echo ""
  echo "──────────────────────────────────────────────"
  bash "$SCRIPT_DIR/$script" "$@"
  [ $? -eq 0 ] && PASS=$(( PASS + 1 )) || FAIL=$(( FAIL + 1 ))
}

# Testy funkcjonalne (single-instance)
run_test test_01_bank_get_empty.sh          "$HOST1"
run_test test_02_bank_set.sh                "$HOST1"
run_test test_03_buy.sh                     "$HOST1"
run_test test_04_sell.sh                    "$HOST1"
run_test test_05_get_wallet.sh              "$HOST1"
run_test test_06_get_wallet_stock.sh        "$HOST1"
run_test test_07_audit_log.sh               "$HOST1"
run_test test_08_error_sell_no_stock.sh     "$HOST1"
run_test test_09_error_buy_unknown_stock.sh "$HOST1"
run_test test_10_error_buy_empty_bank.sh    "$HOST1"
run_test test_11_error_wallet_not_found.sh  "$HOST1"

# Testy współbieżności (multi-instance)
run_test test_12_concurrency_oversell.sh    "$HOST1" "$HOST2" "$HOST3"
run_test test_13_concurrency_same_wallet.sh "$HOST1" "$HOST2" "$HOST3"

# Test chaos — zabija HOST1, uruchamiany jako ostatni
run_test test_14_chaos.sh                   "$HOST1" "$HOST2" "$HOST3"

echo ""
echo "══════════════════════════════════════════════"
echo "PODSUMOWANIE: PASS=$PASS  FAIL=$FAIL"
echo "══════════════════════════════════════════════"
[ $FAIL -eq 0 ] && exit 0 || exit 1
