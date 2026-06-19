#!/bin/sh
set -e

ROOT_DIR=$(cd "$(dirname "$0")/.." && pwd)
cd "$ROOT_DIR"

rm -f .git/hooks/pre-commit
git config core.hooksPath scripts/git-hooks
chmod +x scripts/git-hooks/* 2>/dev/null || true

echo "Git hooks active via core.hooksPath -> scripts/git-hooks (runs lefthook) 🎉"
