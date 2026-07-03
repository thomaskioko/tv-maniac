# Configuration Setup

## Quick start

One command sets up everything:

```bash
./scripts/setup.sh
```

It is idempotent (safe to re-run) and platform-aware:

- **macOS**: full setup, including the iOS toolchain (Homebrew tools, pinned SwiftLint, fastlane), a built KMP XCFramework, and resolved SwiftPM packages.
- **Linux / Windows** (Git Bash or WSL): Android setup only. iOS steps are skipped, since iOS builds require a Mac with Xcode.

Run `./scripts/setup.sh --check` to report environment state without changing anything.

After it finishes, fill in your API keys in `local.properties` (see [Credentials](#credentials)), then build:

```bash
./gradlew :app:assembleDebug          # Android
# iOS (macOS): open ios/tv-maniac.xcodeproj in Xcode
```

### What it does

- Creates `local.properties` from `local.properties.template`, filling in `sdk.dir` when it can detect your Android SDK.
- Installs the git hooks via [lefthook](https://lefthook.dev) (see [Git hooks](#git-hooks)).
- Verifies the Gradle JDK toolchain. Gradle auto-provisions Azul JDK 21, so no manual JDK install is needed.
- macOS only: `brew bundle` (Mint, SwiftFormat, lefthook), `mint bootstrap` (pinned SwiftLint), `bundle install` (fastlane), builds the shared KMP XCFramework (`./scripts/build-kmp-framework.sh`), and resolves SwiftPM packages.

Homebrew is the one macOS prerequisite the script offers to install if missing. Everything else it installs for you.

## Credentials

`local.properties` is gitignored. The build reads these keys from it, or from environment variables of the same name in CI. A key left at its `your_...` placeholder builds but fails at runtime (for example, a TMDB 401), so fill in every value.

### TMDB
[Create a TMDB app](https://www.themoviedb.org/settings/api) to generate `TMDB_API_KEY`.

### Trakt
[Create a Trakt app](https://trakt.tv/oauth/applications/new).
- Redirect URI: `tvmaniac://auth/trakt`
- Copy the Client ID and Client Secret into `TRAKT_CLIENT_ID` and `TRAKT_CLIENT_SECRET`.

### Simkl
[Create a Simkl app](https://simkl.com/settings/developer/).
- Redirect URI: `tvmaniac://auth/simkl`
- Copy the Client ID and Client Secret into `SIMKL_CLIENT_ID` and `SIMKL_CLIENT_SECRET`.

## Firebase Crashlytics (optional)

Crash reporting is disabled unless configured.

- **Android**: Place `google-services.json` in `app/`.
- **iOS**: Place `GoogleService-Info.plist` in `ios/ios/Firebase/Debug/` (and `ios/ios/Firebase/Release/` for release builds).

Both files are gitignored. For CI/CD, store them as base64-encoded secrets.

## Git hooks

Hooks are managed by [lefthook](https://lefthook.dev) and installed by `./scripts/setup.sh`, or directly with `./scripts/install-git-hooks.sh`. Hook behavior is defined in `git-hooks.yml`; a tracked `scripts/git-hooks/pre-commit` (activated through `core.hooksPath`) points lefthook at that file and runs it.

On commit, the pre-commit hook runs only against your **staged** files: it auto-formats them (Spotless for Kotlin, SwiftFormat for Swift), re-stages the results, and lints staged Swift with SwiftLint. Formatting is applied for you, so a clean commit does not bounce back.

- Skip the hook for one commit: `git commit --no-verify`.
- Non-macOS contributors: install lefthook (`brew install lefthook` or a [release binary](https://github.com/evilmartians/lefthook/releases)), then run `./scripts/install-git-hooks.sh`.

## Working on a single iOS package

The Swift packages under `ios/Packages/` resolve the shared Kotlin code through the `TvManiacFramework` package, so any of them opens and builds on its own once the XCFramework is built:

```bash
./scripts/build-kmp-framework.sh   # debug, arm64 simulator (default)
xed ios/Packages/Search
```

Re-run the script after changing Kotlin code; an outdated framework shows up as compile errors against the Kotlin API. `./gradlew clean` deletes the built framework, so re-run the script after that too. Other slices when needed:

```bash
./scripts/build-kmp-framework.sh --platform device
./scripts/build-kmp-framework.sh --configuration release --platform device
```

Full app builds rebuild the framework automatically through the scheme pre-action. See `ios/Packages/TvManiacFramework/README.md` for details.

## iOS Toolchain (macOS Only)

`./scripts/setup.sh` installs this for you. To run the steps manually:

```bash
brew bundle install   # Mint + SwiftFormat
mint bootstrap        # pinned SwiftLint
bundle install        # fastlane
```

`mint bootstrap` installs the SwiftLint version pinned in the Mintfile (currently `0.63.3`). Local developers and CI run the same version, so lint results are reproducible. The Xcode build's Run Script phase invokes SwiftLint via `mint run swiftlint`; CI invokes the matching version via the pinned `ghcr.io/realm/swiftlint:0.63.3` Docker image.

Custom theming rules are at error severity. Reintroducing `@Theme`, hardcoded `.font(.system(size:))`, literal `.foregroundColor(.white/.black/...)`, or `Color(hex:)` outside `DesignSystem/Colors/` fails the build.

If Mint is missing locally, the Xcode build prints a warning and skips the lint phase. CI still enforces the rules, so a pull request that introduces a violation fails before merge. Bump the pinned version by editing both `Mintfile` and the Docker tag in `.github/workflows/ci.yml`.
