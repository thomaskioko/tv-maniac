#!/usr/bin/env bash
set -uo pipefail
script="$(cd "$(dirname "$0")" && pwd)/write-build-number.sh"; repo=$(mktemp -d); trap 'rm -rf "$repo"' EXIT; cd "$repo"
unset GITHUB_OUTPUT GITHUB_STEP_SUMMARY
git init -q; git config user.email t@t; git config user.name t; git config commit.gpgsign false; git config core.hooksPath /dev/null
printf 'VERSION_NUMBER = 0.1.2\nBUILD_NUMBER = 102000\n' > version.txt
git add version.txt; git commit -qm base; git tag v0.1.2
for i in 1 2 3; do git commit -q --allow-empty -m "c$i"; done
fail=0
run() { git checkout -q version.txt; out=$(bash "$script" "$@" 2>&1); code=$?; echo "$code $(grep -o 'build [0-9]*' <<<"$out" | head -1)"; }
expect() { local want="$1"; shift; local got; got=$(run "$@"); if [ "$got" = "$want" ]; then echo "PASS $* -> $got"; else echo "FAIL $* -> $got, want $want"; fail=1; fi; }
expect "0 build 102003"
expect "0 build 102003" 0
expect "0 build 102003" 102002
expect "0 build 102004" 102003
expect "0 build 102010" 102009
expect "1 build " abc
expect "1 build " ""
expect "1 build 103000" 102999
exit $fail
