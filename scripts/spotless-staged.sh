#!/bin/sh
set -eu

[ "$#" -eq 0 ] && exit 0

# spotlessFiles takes a comma-separated list of path regexes; pass only the
# staged Kotlin files so Spotless formats just those instead of every module.
csv=$(printf '%s,' "$@")

exec ./gradlew spotlessApply -PspotlessFiles="${csv%,}" --quiet
