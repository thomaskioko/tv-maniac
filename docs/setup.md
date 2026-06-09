# Configuration Setup

TMDB and Trakt API credentials are required.

## 1. TMDB API Key
[Create a TMDB app](https://www.themoviedb.org/settings/api) to generate a key.

## 2. Trakt OAuth Credentials
[Create a Trakt app](https://trakt.tv/oauth/applications/new).
- **Redirect URI**: `tvmaniac://auth/trakt`
- Note the **Client ID** and **Client Secret**.

## 3. Local Configuration
Create `local.properties` in project root:

```properties
TMDB_API_KEY=your_tmdb_api_key
TRAKT_CLIENT_ID=your_trakt_client_id
TRAKT_CLIENT_SECRET=your_trakt_client_secret
TRAKT_REDIRECT_URI=tvmaniac://auth/trakt
```

**CI/CD**: Use identical environment variable names.

## 4. Firebase Crashlytics (Optional)
Crash reporting is disabled unless configured.

- **Android**: Place `google-services.json` in `app/`.
- **iOS**: Place `GoogleService-Info.plist` in `ios/ios/Resources/`.

Both files are gitignored. For CI/CD, store as base64-encoded secrets.

## 5. iOS Toolchain (macOS Only)

iOS development needs Xcode 16 or later plus the command-line tools listed in the project Brewfile, then a pinned SwiftLint installed via Mint:

```bash
brew bundle install
mint bootstrap
```

`brew bundle install` installs:

- `mint`. Pins Swift command-line tools to versions declared in the project Mintfile. Idempotent across re-runs.
- `swiftformat`. Formats Swift code. Used by `fastlane format_swift_code` and `fastlane check_swift_format`.

`mint bootstrap` reads the project Mintfile and installs the pinned SwiftLint version (currently `0.63.2`). Local developers and CI run the same version, so lint results are reproducible. The Xcode build's Run Script phase invokes SwiftLint via `mint run swiftlint`; CI invokes the matching version via the pinned `ghcr.io/realm/swiftlint:0.63.2` Docker image.

Custom theming rules are at error severity. Reintroducing `@Theme`, hardcoded `.font(.system(size:))`, literal `.foregroundColor(.white/.black/...)`, or `Color(hex:)` outside `DesignSystem/Colors/` fails the build.

If Mint is missing locally, the Xcode build prints a warning and skips the lint phase. CI still enforces the rules, so a pull request that introduces a violation fails before merge. Bump the pinned version by editing both `Mintfile` and the Docker tag in `.github/workflows/ci.yml`.
