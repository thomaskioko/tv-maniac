#!/usr/bin/env bash
#
# Makes the KMP frameworks (TvManiac + i18n) available to the iOS build.
#
# Runs as an Xcode scheme build pre-action, so it inherits the Xcode build
# environment (CONFIGURATION, SDK_NAME, BUILT_PRODUCTS_DIR).
#
# Default (local dev, release/beta/daily builds): links and embeds the
# frameworks via Gradle, exactly as before.
#
# CI fast path: build-ios-framework links the frameworks once and caches
# ios-framework/build/xcode-frameworks + i18n/generator/build/xcode-frameworks.
# Consumer jobs set TVMANIAC_SKIP_FRAMEWORK_BUILD=1; this script then copies the
# cached frameworks into BUILT_PRODUCTS_DIR (where SwiftPM package targets resolve
# them) instead of re-running the expensive Kotlin/Native link. The copy is
# required even for a static framework: local package targets (e.g. CoreKit) do
# not inherit the app target's FRAMEWORK_SEARCH_PATHS and find the framework only
# in BUILT_PRODUCTS_DIR.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$REPO_ROOT"

TV_SRC="ios-framework/build/xcode-frameworks/${CONFIGURATION}/${SDK_NAME}"
I18N_SRC="i18n/generator/build/xcode-frameworks/${CONFIGURATION}/${SDK_NAME}"

if [ "${TVMANIAC_SKIP_FRAMEWORK_BUILD:-}" = "1" ] && [ -d "${TV_SRC}/TvManiac.framework" ]; then
  echo "note: using prebuilt KMP framework from cache"
  mkdir -p "${BUILT_PRODUCTS_DIR}"
  ditto "${TV_SRC}/TvManiac.framework" "${BUILT_PRODUCTS_DIR}/TvManiac.framework"
  ditto "${I18N_SRC}/i18n.framework" "${BUILT_PRODUCTS_DIR}/i18n.framework"
else
  ./gradlew \
    :ios-framework:embedAndSignAppleFrameworkForXcode \
    :i18n:generator:embedAndSignAppleFrameworkForXcode \
    -Papp.enableIos=true
fi
