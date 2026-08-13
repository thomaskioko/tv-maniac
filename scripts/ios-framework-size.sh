#!/usr/bin/env bash
#
# Reports the three size numbers tracked for the iOS framework: __text bytes
# (real code), __objc_* section sizes (interop metadata), and the Obj-C bridge
# symbol count (exported surface). Measure a release link only; Kotlin/Native
# runs no dead code elimination in debug builds. The framework file size is
# deliberately not reported: it is ~40% DWARF and moves for unrelated reasons.
#
# Usage: ./scripts/ios-framework-size.sh [--csv] [path-to-framework-binary]
#
# --csv prints "metric,value" lines instead of the section listing: one
# "section/<name>" line for each Mach-O section of the framework image plus
# "bridge/symbols". Metric names are path shaped so a dashboard can split
# them into levels; scripts/ios-framework-size-analysis.py emits "module/<path>" rows
# in the same format.
set -euo pipefail

CSV=0
POSITIONAL=()
for ARG in "$@"; do
  case "$ARG" in
    --csv) CSV=1 ;;
    *) POSITIONAL+=("$ARG") ;;
  esac
done

F="${POSITIONAL[0]:-ios-framework/build/bin/iosArm64/releaseFramework/TvManiac.framework/TvManiac}"
if [ ! -f "$F" ]; then
  echo "error: $F not found — run: ./gradlew :ios-framework:linkReleaseFrameworkIosArm64 -Papp.enableIos=true" >&2
  exit 1
fi

SECTIONS="$(size -m "$F" | awk '/__text|__objc_/')"
if [ -z "$SECTIONS" ]; then
  echo "error: no __text or __objc_ sections found in $F" >&2
  exit 1
fi

BRIDGES="$(nm -arch arm64 "$F" | grep -cE 'objc2kotlin|kotlin2objc')"

if [ "$CSV" -eq 1 ]; then
  size -m "$F" | awk '
    /Section \(/ {
      name = $2 $3
      gsub(/[(),:]/, "", name)
      sub(/^__[A-Z_]+__/, "__", name)
      value = $NF
      if (value !~ /^[0-9]+$/) value = $(NF - 1)
      if (!seen[name]++) printf "section/%s,%s\n", name, value
    }'
  printf 'bridge/symbols,%s\n' "$BRIDGES"
else
  printf '%s\n' "$SECTIONS"
  printf 'objc bridge symbols: %s\n' "$BRIDGES"
fi
