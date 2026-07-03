#!/usr/bin/env bash
#
# One-command developer setup for TvManiac.
#
# Cross-platform steps run everywhere (local.properties, git hooks, JDK via
# Gradle). The Swift toolchain and iOS steps run only on macOS, since iOS builds
# require Xcode. Non-macOS contributors get a complete Android setup.
#
# Usage:
#   ./scripts/setup.sh           full setup
#   ./scripts/setup.sh --check   report environment state, change nothing
#   ./scripts/setup.sh --help
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$ROOT_DIR"

OS="$(uname -s)"
MODE="setup"
case "${1:-}" in
  --check|doctor) MODE="check" ;;
  -h|--help) sed -n '3,12p' "$0" | sed 's/^# \{0,1\}//'; exit 0 ;;
  "") ;;
  *) echo "Unknown option: $1 (try --help)"; exit 2 ;;
esac

step() { printf '\n\033[1m▸ %s\033[0m\n' "$*"; }
ok()   { printf '  \033[32m✓\033[0m %s\n' "$*"; }
warn() { printf '  \033[33m!\033[0m %s\n' "$*"; }
info() { printf '    %s\n' "$*"; }

is_macos() { [ "$OS" = "Darwin" ]; }

detect_android_sdk() {
  if [ -n "${ANDROID_HOME:-}" ]; then echo "$ANDROID_HOME"; return; fi
  if [ -n "${ANDROID_SDK_ROOT:-}" ]; then echo "$ANDROID_SDK_ROOT"; return; fi
  if is_macos && [ -d "$HOME/Library/Android/sdk" ]; then echo "$HOME/Library/Android/sdk"; return; fi
  [ -d "$HOME/Android/Sdk" ] && echo "$HOME/Android/Sdk"
}

ensure_local_properties() {
  step "Credentials (local.properties)"
  if [ -f local.properties ]; then
    ok "local.properties already exists (left unchanged)"
    return
  fi
  cp local.properties.template local.properties
  local sdk; sdk="$(detect_android_sdk)"
  if [ -n "$sdk" ]; then
    printf 'sdk.dir=%s\n' "$sdk" >> local.properties
    ok "Created local.properties (sdk.dir=$sdk)"
  else
    ok "Created local.properties from template"
  fi
  warn "Fill in your API keys in local.properties (see docs/setup.md)"
}

ensure_hooks() {
  step "Git hooks"
  "$SCRIPT_DIR/install-git-hooks.sh"
}

ensure_macos_toolchain() {
  is_macos || { step "Swift toolchain"; info "Skipped (not macOS)"; return; }
  step "Swift toolchain (macOS)"
  if ! command -v brew >/dev/null 2>&1; then
    if [ -t 0 ]; then
      info "Homebrew not found. Installing it..."
      /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
      eval "$(/opt/homebrew/bin/brew shellenv 2>/dev/null || /usr/local/bin/brew shellenv 2>/dev/null || true)"
    else
      warn "Homebrew not found. Install from https://brew.sh and re-run. Skipping Swift tools + iOS."
      return
    fi
  fi
  brew bundle install && ok "brew bundle (mint, swiftformat)" || warn "brew bundle failed"
  mint bootstrap --link && ok "mint bootstrap (SwiftLint pinned)" || warn "mint bootstrap failed"
  if command -v bundle >/dev/null 2>&1; then
    bundle install && ok "bundle install (fastlane)" || warn "bundle install failed"
  else
    warn "Ruby bundler not found (needs ruby $(cat .ruby-version 2>/dev/null)). Run 'bundle install' once available."
  fi
}

ensure_ios() {
  is_macos || return
  step "iOS"
  if ! command -v xcodebuild >/dev/null 2>&1; then
    warn "Xcode not found. Install Xcode 16+ to build iOS."
    return
  fi
  info "Building the shared KMP XCFramework (first run is slow)..."
  if ./scripts/build-kmp-framework.sh; then
    ok "KMP XCFramework built for SwiftPM"
  else
    warn "Framework build failed; run ./scripts/build-kmp-framework.sh before opening Xcode"
    return
  fi
  info "Resolving Swift Package dependencies..."
  if xcodebuild -resolvePackageDependencies -project ios/tv-maniac.xcodeproj -scheme tv-maniac >/dev/null 2>&1; then
    ok "SwiftPM resolved"
  else
    warn "Could not resolve SwiftPM packages (open ios/tv-maniac.xcodeproj in Xcode to retry)"
  fi
}

ensure_jdk() {
  step "JDK"
  info "Verifying the Gradle toolchain (auto-provisions Azul JDK 21)..."
  if ./gradlew --version >/dev/null 2>&1; then
    ok "Gradle + JDK ready"
  else
    warn "Gradle could not start. Check your Java install or network."
  fi
}

summary() {
  step "Summary"
  printf "    OS                : %s\n" "$OS"
  if [ "$(git config core.hooksPath 2>/dev/null)" = "scripts/git-hooks" ]; then
    printf "    git hooks         : lefthook (scripts/git-hooks)\n"
  else
    printf "    git hooks         : \033[33mnot installed (run ./scripts/install-git-hooks.sh)\033[0m\n"
  fi
  if [ -f local.properties ]; then
    local placeholders; placeholders="$(grep -c '=your_' local.properties 2>/dev/null || true)"; placeholders="${placeholders:-0}"
    if [ "$placeholders" -gt 0 ]; then
      printf "    local.properties  : present, \033[33m%s key(s) still placeholder\033[0m\n" "$placeholders"
    else
      printf "    local.properties  : present, keys filled in\n"
    fi
  else
    printf "    local.properties  : \033[33mmissing\033[0m\n"
  fi
  if is_macos; then
    printf "    brew / mint       : %s / %s\n" "$(command -v brew >/dev/null 2>&1 && echo ok || echo missing)" "$(command -v mint >/dev/null 2>&1 && echo ok || echo missing)"
    printf "    lefthook / xcode  : %s / %s\n" "$(command -v lefthook >/dev/null 2>&1 && echo ok || echo missing)" "$(command -v xcodebuild >/dev/null 2>&1 && echo ok || echo missing)"
  else
    printf "    iOS               : needs macOS + Xcode (Android setup is complete)\n"
  fi
  printf "\n  Build Android: ./gradlew :app:assembleDebug"
  is_macos && printf "    |    iOS: open ios/tv-maniac.xcodeproj in Xcode"
  printf "\n"
}

if [ "$MODE" = "check" ]; then
  summary
  exit 0
fi

printf "\033[1mTvManiac setup\033[0m\n"
ensure_local_properties
ensure_hooks
ensure_jdk
ensure_macos_toolchain
ensure_ios
summary
printf "\n\033[32mDone.\033[0m Fill in any placeholder keys in local.properties, then build.\n"
