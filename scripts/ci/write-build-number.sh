#!/usr/bin/env bash
set -euo pipefail

version=$(grep 'VERSION_NUMBER' version.txt | sed 's/.*= *//')
committed_build=$(grep 'BUILD_NUMBER' version.txt | sed 's/.*= *//')
IFS='.' read -r major minor patch <<< "$version"
base=$((major * 10000000 + minor * 100000 + patch * 1000))
tag="v${version}"

if ! git rev-parse -q --verify "refs/tags/${tag}" > /dev/null; then
  echo "error: tag ${tag} not found — nightly build numbers are derived from commits since the release tag" >&2
  exit 1
fi

count=$(git rev-list --count "${tag}..HEAD")
if ((count > 999)); then
  echo "error: ${count} commits since ${tag} exceed the 999 build-number slots for ${version} — cut a release first" >&2
  exit 1
fi

build=$((base + count))
if ((build <= committed_build)); then
  echo "error: derived build ${build} is not above the last released build ${committed_build} — nothing new to publish" >&2
  exit 1
fi

sed -i.bak "s/^BUILD_NUMBER.*/BUILD_NUMBER = ${build}/" version.txt
rm version.txt.bak

sha=$(git rev-parse --short HEAD)
echo "App version: ${version} (build ${build}, ${count} commits since ${tag}, ${sha})"

if [ -n "${GITHUB_OUTPUT:-}" ]; then
  {
    echo "version=${version}"
    echo "build=${build}"
  } >> "$GITHUB_OUTPUT"
fi

if [ -n "${GITHUB_STEP_SUMMARY:-}" ]; then
  echo "**${version}** — build \`${build}\` (${count} commits since ${tag}, \`${sha}\`)" >> "$GITHUB_STEP_SUMMARY"
fi
