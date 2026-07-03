#!/usr/bin/env bash
#
# Builds the KMP framework (TvManiac) and packages it as an XCFramework at
# ios/Packages/TvManiacFramework/Frameworks/, the one location the
# TvManiacFramework Swift package reads it from. The app, any package opened
# on its own (`xed ios/Packages/<Name>`), and CI all resolve it from there.
#
# Ways to run:
#   - Xcode scheme pre-action: no arguments; CONFIGURATION and SDK_NAME come
#     from the Xcode build environment (Debug/Release, simulator/device).
#   - By hand: ./scripts/build-kmp-framework.sh
#       [--configuration debug|release] [--platform simulator|device]
#     Defaults: debug, simulator.
#
# Gradle relinks only the slice being built (incremental); the XCFramework is
# then put together again from every slice already built for that
# configuration, so switching between simulator and device stays cheap.
#
# CI: TVMANIAC_SKIP_FRAMEWORK_BUILD=1 exits early when the XCFramework
# restored from cache already contains the required slice.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$REPO_ROOT"
DEST="$REPO_ROOT/ios/Packages/TvManiacFramework/Frameworks"

CONFIG=""
PLATFORM=""
while [ $# -gt 0 ]; do
  case "$1" in
    --configuration) CONFIG="$(printf '%s' "$2" | tr '[:upper:]' '[:lower:]')"; shift 2 ;;
    --platform)      PLATFORM="$2"; shift 2 ;;
    *) echo "error: unknown option: $1 (see header of $0)" >&2; exit 2 ;;
  esac
done
[ -n "$CONFIG" ] || CONFIG="$(printf '%s' "${CONFIGURATION:-debug}" | tr '[:upper:]' '[:lower:]')"
if [ -z "$PLATFORM" ]; then
  case "${SDK_NAME:-iphonesimulator}" in
    iphoneos*) PLATFORM="device" ;;
    *)         PLATFORM="simulator" ;;
  esac
fi

case "$PLATFORM" in
  simulator) SLICE="ios-arm64-simulator"; TARGET="IosSimulatorArm64" ;;
  device)    SLICE="ios-arm64";           TARGET="IosArm64" ;;
  *) echo "error: unknown platform '$PLATFORM' (expected simulator or device)" >&2; exit 2 ;;
esac
# Xcode injects CONFIGURATION verbatim; an unknown value (a future Beta/Staging
# config) falls back to Debug instead of hard-failing the scheme pre-action.
case "$CONFIG" in
  debug)   TASK_CONFIG="Debug" ;;
  release) TASK_CONFIG="Release" ;;
  *)
    echo "warning: unknown configuration '$CONFIG'; defaulting to Debug" >&2
    CONFIG="debug"
    TASK_CONFIG="Debug"
    ;;
esac

if [ "${TVMANIAC_SKIP_FRAMEWORK_BUILD:-}" = "1" ] \
    && [ -d "$DEST/TvManiac.xcframework/$SLICE" ]; then
  echo "note: using prebuilt KMP XCFramework from $DEST"
  exit 0
fi

./gradlew \
  ":ios-framework:link${TASK_CONFIG}Framework${TARGET}" \
  -Papp.enableIos=true

bundle() {
  local name="$1" module_dir="$2"
  local args=()
  local konan_target
  for konan_target in iosSimulatorArm64 iosArm64; do
    local fw="$REPO_ROOT/$module_dir/build/bin/$konan_target/${CONFIG}Framework/$name.framework"
    [ -d "$fw" ] && args+=(-framework "$fw")
  done
  local tmp
  tmp="$(mktemp -d)"
  trap 'rm -rf "$tmp"' RETURN
  xcrun xcodebuild -create-xcframework "${args[@]}" -output "$tmp/$name.xcframework" >/dev/null
  mkdir -p "$DEST"
  rm -rf "$DEST/$name.xcframework"
  mv "$tmp/$name.xcframework" "$DEST/$name.xcframework"
}

bundle "TvManiac" "ios-framework"

echo "note: KMP XCFramework ($CONFIG) written to $DEST"
