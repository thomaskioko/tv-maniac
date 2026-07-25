#!/usr/bin/env bash
#
# Prints the content hash keying the CI cache for the TvManiac XCFramework.
# A script rather than hashFiles() because hashFiles has no exclusion syntax.
# EXCLUDES must track the compile closure in ios-framework/build.gradle.kts.
set -euo pipefail

cd "$(dirname "$0")/.."

EXCLUDES=(
  ':!:app/**'
  ':!:benchmark/**'
  ':!:android-designsystem/**'
  ':!:features/*/ui/**'
  ':!:*/src/*Test*/**'
  ':!:*/src/androidMain/**'
)

INCLUDES=(
  '*.kt'
  '*.kts'
  'gradle/libs.versions.toml'
  'gradle.properties'
  'gradle/wrapper/gradle-wrapper.properties'
)

# shasum, not sha256sum: macOS runners ship the former only.
git ls-files -z -- "${INCLUDES[@]}" "${EXCLUDES[@]}" \
  | xargs -0 shasum -a 256 \
  | shasum -a 256 \
  | cut -d' ' -f1
