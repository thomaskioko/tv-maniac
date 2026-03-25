# Release Process

TvManiac uses an automated release pipeline that builds, signs, and deploys to both Google Play Store and Apple App Store. Production releases are triggered by tags pushed from the local release task. Internal/beta releases are triggered manually via GitHub Actions.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Create a Production Release](#create-a-production-release)
- [Create an Internal Release on CI](#create-an-internal-release-on-ci)
- [Promote a Release on CI](#promote-a-release-on-ci)
- [Promote a Release Locally](#promote-a-release-locally)
- [Version Bumping](#version-bumping)
- [Beta Releases](#beta-releases)
- [Version Scheme](#version-scheme)
- [Hotfix](#hotfix)
- [Changelog](#changelog)
- [Signing & Secrets](#signing--secrets)

---

## Prerequisites

Before you can create or promote releases, make sure the following are set up:

- [git-cliff](https://git-cliff.org/) installed: `brew install git-cliff`
- Ruby and Fastlane configured: `bundle install`
- For CI releases: GitHub secrets configured (see [Signing & Secrets](#signing--secrets))
- For local releases: signing properties in `release/signing.properties` (see [release-signing-setup.md](../tasks/release-signing-setup.md))

---

## Create a Production Release

Production releases are created locally and triggered on CI by pushing a version tag. The local release task handles version bumping, changelog generation, committing, tagging, and pushing.

### Step 1: Create the release locally

```bash
./gradlew :app:release -Pi                    # Interactive (recommended)
./gradlew :app:release -Ptype=patch           # Silent: commits, tags, and pushes automatically
./gradlew :app:release -PdryRun -Ptype=patch  # Dry-run: preview without making changes
```

**Interactive mode** walks you through:

1. Showing current version and recent tags
2. Running pre-flight checks (on `main`, clean tree, up-to-date with remote, no duplicate tag)
3. Prompting for bump type (major / minor / patch)
4. Previewing the changelog
5. Asking for confirmation before committing and tagging
6. Pushing to origin

### Step 2: CI builds and deploys automatically

When the tag (e.g., `v0.1.3`) is pushed, CI automatically:

1. **Build Android**: Builds a signed release AAB + APK, deploys to Play Store production at 0.1% rollout, and distributes to Firebase App Distribution
2. **Build iOS**: Builds a signed release IPA via Fastlane Match and uploads to TestFlight
3. **GitHub Release**: Creates a GitHub Release with the changelog and APK attached

Platform builds are independent. If one platform fails, the other still deploys.

> **Note:** The release build variant is disabled by default for faster local development (`app.debugOnly=true`). The release task handles this automatically.

---

## Create an Internal Release on CI

Internal releases deploy beta builds to Play Store internal track and TestFlight for testing. The workflow bumps the beta build number, commits to `main`, then builds and deploys.

Go to **Actions > Internal Release > Run workflow**, or use the CLI:

```bash
gh workflow run internal-release.yml
gh workflow run internal-release.yml -f skip_ios=true
```

| Input | Required | Options |
|---|---|---|
| skip_android | no | default: false |
| skip_ios | no | default: false |

**What happens:**

1. **Prepare**: Runs `bumpVersion -Ptype=beta`, increments `BUILD_NUMBER`, commits and pushes to `main`
2. **Build Android**: Builds a signed release AAB with `-beta` suffix, deploys to Play Store internal track
3. **Build iOS**: Builds a signed release IPA, uploads to TestFlight

---

## Promote a Release on CI

After a release is deployed, use promotion to ramp up the rollout percentage or submit for App Store review.

Go to **Actions > Promote Release > Run workflow**, or use the CLI:

```bash
# Ramp Android rollout to 50%
gh workflow run promote-release.yml \
  -f platform=android \
  -f android_from_track=production \
  -f android_to_track=production \
  -f android_rollout=0.5

# Full rollout (100%)
gh workflow run promote-release.yml \
  -f platform=android \
  -f android_from_track=production \
  -f android_to_track=production \
  -f android_rollout=1.0

# Submit iOS to App Store review
gh workflow run promote-release.yml \
  -f platform=ios \
  -f ios_submit_for_review=true
```

| Input | Options | Default |
|---|---|---|
| platform | `android`, `ios`, `both` | required |
| android_from_track | `internal`, `beta`, `production` | `internal` |
| android_to_track | `beta`, `production` | `production` |
| android_rollout | `0.001` to `1.0` | `0.001` (0.1%) |
| ios_submit_for_review | boolean | false |

---

## Promote a Release Locally

Requires the Play Store service account JSON and App Store Connect API key to be available locally.

**Android:**

```bash
bundle exec fastlane android promote from:internal to:production rollout:0.001
bundle exec fastlane android promote from:production to:production rollout:1.0
```

**iOS:**

```bash
bundle exec fastlane ios deploy_app_store
```

---

## Version Bumping

`version.txt` is the single source of truth for both Android and iOS versions. Use `bumpVersion` when you need to bump the version without triggering a full release (e.g., for CI orchestration).

```bash
./gradlew :app:bumpVersion -Ptype=patch   # 0.1.2 > 0.1.3, BUILD = 103000
./gradlew :app:bumpVersion -Ptype=minor   # 0.1.2 > 0.2.0, BUILD = 200000
./gradlew :app:bumpVersion -Ptype=major   # 0.1.2 > 1.0.0, BUILD = 10000000
./gradlew :app:bumpVersion -Ptype=beta    # 0.1.2 stays,    BUILD = 102001
```

---

## Beta Releases

Beta builds let you upload multiple test versions to Play Store and TestFlight without burning version numbers. This is useful for internal testing before a production release.

`bumpVersion -Ptype=beta` increments `BUILD_NUMBER` by 1 without changing `VERSION_NUMBER`. The version name gets a `-beta` suffix automatically.

**Example lifecycle:**

```
0.1.2 / 102000  >  beta   >  0.1.2 / 102001 (internal release)
0.1.2 / 102001  >  beta   >  0.1.2 / 102002 (internal release)
0.1.2 / 102002  >  patch  >  0.1.3 / 103000 (production release via tag)
```

Each version reserves 1000 build number slots. Production resets to `X000`, betas use `X001` through `X999`.

---

## Version Scheme

All versioning is driven by `version.txt` at the project root, which contains `VERSION_NUMBER` and `BUILD_NUMBER`. Both Android (`versionCode` / `versionName`) and iOS (`CURRENT_PROJECT_VERSION` / `MARKETING_VERSION`) read from this file.

**Build number formula:** `(major * 10,000,000) + (minor * 100,000) + (patch * 1,000)`

| | Production | Internal/Beta | Alpha |
|---|---|---|---|
| Version name | `0.1.3` | `0.1.2-beta` | `0.1.2-alpha` |
| Build number | `103000` | `102001`, `102002`... | from `version.txt` |
| Tag | `v0.1.3` | No tag | No tag |
| Play Store | production (0.1% rollout) | internal track | Not uploaded |
| Firebase | Yes | No | Yes |
| Trigger | Tag push | `workflow_dispatch` | Push to `main` |

The `-beta` suffix is controlled by `app.versionSuffix` in `gradle.properties` (default: `-beta`). Production releases override it to empty via `-Papp.versionSuffix=`.

---

## Hotfix

When a critical bug is found in production, create a hotfix branch from the release tag, apply the fix, and release a patch version.

1. Create a branch from the release tag:
   ```bash
   git checkout -b hotfix/v0.2.1 v0.2.0
   ```
2. Apply the fix and commit with conventional format (e.g., `fix(auth): handle expired token`)
3. Release with a patch bump:
   ```bash
   ./gradlew :app:release -Ptype=patch
   ```
4. Merge the hotfix branch back to `main`

---

## Changelog

Changelogs are auto-generated from [conventional commits](https://www.conventionalcommits.org/) using [git-cliff](https://git-cliff.org/). Use the standard commit format `type(scope): message`.

Examples: `feat(discover): add trending carousel`, `fix(auth): handle token refresh`

Release commits (prefixed with `release:`) are automatically excluded.

---

## Signing & Secrets

Release builds require signing keys (Android keystore, iOS certificates) and store credentials. These are encrypted and stored in the repository.

See [release-signing-setup.md](../tasks/release-signing-setup.md) for the full setup guide and list of required GitHub secrets.
