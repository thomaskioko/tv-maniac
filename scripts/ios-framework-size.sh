#!/usr/bin/env bash
#
# Reports the three size numbers tracked for the iOS framework: __text bytes
# (real code), __objc_* section sizes (interop metadata), and the Obj-C bridge
# symbol count (exported surface). Measure a release link only; Kotlin/Native
# runs no dead code elimination in debug builds. The framework file size is
# deliberately not reported: it is ~40% DWARF and moves for unrelated reasons.
#
# Usage: ./scripts/ios-framework-size.sh [path-to-framework-binary]
set -euo pipefail

F="${1:-ios-framework/build/bin/iosArm64/releaseFramework/TvManiac.framework/TvManiac}"
if [ ! -f "$F" ]; then
  echo "error: $F not found — run: ./gradlew :ios-framework:linkReleaseFrameworkIosArm64 -Papp.enableIos=true" >&2
  exit 1
fi

SECTIONS="$(size -m "$F" | awk '/__text|__objc_/')"
if [ -z "$SECTIONS" ]; then
  echo "error: no __text or __objc_ sections found in $F" >&2
  exit 1
fi
printf '%s\n' "$SECTIONS"
printf 'objc bridge symbols: '
nm -arch arm64 "$F" | grep -cE 'objc2kotlin|kotlin2objc'
