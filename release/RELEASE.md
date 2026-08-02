# Release Process

TvManiac uses an automated release pipeline that builds, signs, and deploys to both Google Play Store and Apple App Store. Production releases are triggered by tags pushed from the local release task and roll out through approval gates. Daily builds run on a weekday schedule; beta releases are triggered manually.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Create a Production Release](#create-a-production-release)
- [Trigger Beta Release on CI](#trigger-beta-release-on-ci)
- [Daily Builds](#daily-builds)
- [Gradual Rollout](#gradual-rollout)
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
- For local releases: signing properties in `release/signing.properties` (setup guide lives in the Obsidian vault under `TvManiac/`)

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

When the tag (e.g., `v0.1.3`) is pushed, two independent workflows run: `release-android.yml` and `release-ios.yml`. Each validates that the tag matches `version.txt`, builds once, and fans out to deploy jobs, so a failed upload retries with `gh run rerun <run-id> --failed` without rebuilding.

**Release Android**: builds the signed AAB + APK, then in parallel deploys to Play Store production at 0.1% rollout, distributes the APK to Firebase App Distribution, and creates a draft GitHub Release with the changelog and APK attached. The run then ramps the rollout through approval-gated jobs (see [Gradual Rollout](#gradual-rollout)).

**Release iOS**: builds the signed IPA, uploads it to TestFlight, then pauses at an approval gate before submitting to App Store review with phased release enabled.

Platform workflows are independent. If one platform fails, the other still deploys.

> **Note:** The release build variant is disabled by default for faster local development (`app.debugOnly=true`). The release task handles this automatically.

---

## Trigger Beta Release on CI

Beta releases deploy builds to the Play Store open testing track and TestFlight for wider testing. Nothing is committed or pushed — the build number is derived the same way as daily builds.

Go to **Actions > Beta Release > Run workflow**, or use the CLI:

```bash
gh workflow run beta-release.yml
gh workflow run beta-release.yml -f skip_ios=true
```

| Input        | Required | Options        |
|--------------|----------|----------------|
| skip_android | no       | default: false |
| skip_ios     | no       | default: false |

**What happens:**

1. **Version**: `scripts/ci/write-build-number.sh` derives the build number and writes `version.txt` in the runner's workspace only.
2. **Build**: Per-platform build jobs produce the signed artifacts (Android with a `-beta` suffix) and upload them as workflow artifacts.
3. **Deploy**: Separate jobs publish them — Android to the Play Store open testing track and Firebase App Distribution, iOS to TestFlight.

A beta and a daily build cut from the same commit derive the same build number, and the stores reject the duplicate upload. Land a commit first, or rerun after one lands.

## Daily Builds

Daily builds run per platform on weekdays at 1:00 AM UTC (3:00 AM Berlin in summer, 2:00 AM in winter), and can also be triggered manually. Each workflow is self-contained, so a failed platform reruns alone.

```bash
gh workflow run daily-build-android.yml
gh workflow run daily-build-ios.yml
```

**What happens:**

1. **Check**: Scheduled runs skip when `main` has no new commits since the last successful daily build. Manual runs always build.
2. **Version**: `scripts/ci/write-build-number.sh` derives the build number as `base(version) + commits since the release tag` and writes it to `version.txt` in the runner's workspace only — nothing is committed or pushed.
3. **Build**: One job builds the signed artifacts with a `-dev` suffix (Android AAB and APK, iOS IPA) and uploads them as workflow artifacts.
4. **Deploy**: Separate jobs download the artifacts and publish them — Android to the Play Store internal track, Firebase App Distribution, and the rolling `nightly` GitHub release in parallel, iOS to TestFlight. The nightly release keeps one stable download URL whose APK is replaced on every run.

Build and deploy are separate jobs, so a failed upload retries without rebuilding:

```bash
gh run rerun <run-id> --failed
```

The derived build number stays inside the version's 999-slot budget; the script fails the run when the budget is exhausted, which means it is time to cut a release.

---

## Gradual Rollout

The rollout lives inside the `Release Android` run as a chain of approval-gated jobs. After production deploys at 0.1%, the run pauses at each ramp stage until you approve it in the Actions UI. Check Play Vitals and Crashlytics before approving — you are the crash gate.

| Stage       | Android          | iOS                            |
|-------------|------------------|--------------------------------|
| Release     | 0.1% (automatic) | TestFlight (automatic)         |
| Gate 1      | 1%               | Submit for App Store review    |
| Gate 2      | 10%              | Apple manages phased rollout   |
| Gate 3      | 50%              |                                |
| Gate 4      | 100%             |                                |

Approve each stage on your own schedule — a pending gate waits up to 30 days. Rejecting a gate stops the chain; ship a fixed patch release instead. On iOS, the single gate submits the TestFlight build for App Store review with phased release enabled; Apple then ramps automatically (1% > 2% > 5% > 10% > 20% > 50% > 100% over 7 days).

The gates pause because the `production` environment in **Settings > Environments** requires your review. To jump to a different percentage outside the chain, use the local promote lane below.

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
```

`-Ptype=beta` still exists but is legacy: CI derives beta and daily build numbers from git instead. Don't commit beta bumps — a raised `BUILD_NUMBER` floor can block derived builds until enough commits land.

---

## Beta Releases

Beta builds let you upload multiple test versions to Play Store and TestFlight without burning version numbers. This is useful for internal testing before a production release.

Beta and daily build numbers are derived, not committed: `base(version) + commits since the release tag`. The committed `BUILD_NUMBER` in `version.txt` only changes on production releases.

**Example lifecycle:**

```
0.1.2 / 102000 released  >  40 commits land   >  daily/beta builds derive 102001…102040
patch release            >  0.1.3 / 103000     >  derivation continues from v0.1.3
```

Each version reserves 1000 build number slots. Production takes `X000`, derived builds use `X001` through `X999`. The version script fails the run when the budget is exhausted — time to cut a release.

---

## Version Scheme

All versioning is driven by `version.txt` at the project root, which contains `VERSION_NUMBER` and `BUILD_NUMBER`. Both Android (`versionCode` / `versionName`) and iOS (`CURRENT_PROJECT_VERSION` / `MARKETING_VERSION`) read from this file.

**Build number formula:** `(major * 10,000,000) + (minor * 100,000) + (patch * 1,000)`

|              | Production                | Beta                     | Daily                    |
|--------------|---------------------------|--------------------------|--------------------------|
| Version name | `0.1.3`                   | `0.1.2-beta`             | `0.1.2-dev`              |
| Build number | `103000` (committed)      | derived `102001`–`102999` | derived `102001`–`102999` |
| Tag          | `v0.1.3`                  | No tag                   | No tag                   |
| Play Store   | production (0.1% rollout) | open testing track       | internal track           |
| Firebase     | Yes                       | Yes                      | Yes                      |
| Trigger      | Tag push                  | `workflow_dispatch`      | Schedule / manual        |

Version-name suffixes are passed per workflow: production overrides to empty, daily builds use `-dev`, betas use `-beta`. Local builds default to `-debug` via `gradle.properties`.

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

The full setup guide and list of required GitHub secrets live in the Obsidian vault under `TvManiac/`.
