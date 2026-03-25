# Release Process

How to release TvManiac to the Google Play Store and Apple App Store.

## CI Release

Go to **Actions → Create Release → Run workflow** on GitHub.

| Input | Required | Options |
|---|---|---|
| bump_type | yes | `patch`, `minor`, `major`, `beta` |
| skip_android | no | default: false |
| skip_ios | no | default: false |

The workflow runs CI validation, bumps version, generates changelog, builds both platforms, deploys to stores, and creates a GitHub Release. Platform builds are independent — one failing doesn't block the other.

## Local Release

```bash
./gradlew :app:release -Pi                    # Interactive (recommended)
./gradlew :app:release -Ptype=patch           # Silent
./gradlew :app:release -PdryRun -Ptype=patch  # Dry-run (preview only)
```

Interactive mode walks you through pre-flight checks, bump type selection, changelog preview, and confirmation before committing and tagging.

## Version Bumping

```bash
./gradlew :app:bumpVersion -Ptype=patch   # 0.1.2 → 0.1.3, BUILD = 103000
./gradlew :app:bumpVersion -Ptype=minor   # 0.1.2 → 0.2.0, BUILD = 200000
./gradlew :app:bumpVersion -Ptype=major   # 0.1.2 → 1.0.0, BUILD = 10000000
./gradlew :app:bumpVersion -Ptype=beta    # 0.1.2 stays,    BUILD = 102001
```

## Beta Releases

Beta builds allow multiple test uploads without burning version numbers. `bumpVersion -Ptype=beta` increments `BUILD_NUMBER` by 1 without changing `VERSION_NUMBER`.

```
0.1.2 / 102000  →  beta   →  0.1.2 / 102001 (v0.1.2-beta.1)
0.1.2 / 102001  →  beta   →  0.1.2 / 102002 (v0.1.2-beta.2)
0.1.2 / 102002  →  patch  →  0.1.3 / 103000 (v0.1.3)
```

Each version reserves 1000 build number slots. Production resets to `X000`, betas use `X001`-`X999`.

```bash
./gradlew :app:release -Ptype=beta           # Beta release from main
./gradlew :app:release -Pbeta -Ptype=beta    # Beta from any branch
```

## Version Scheme

**Source of truth**: `version.txt` (`VERSION_NUMBER` + `BUILD_NUMBER`)

Build number formula: `(major * 10,000,000) + (minor * 100,000) + (patch * 1,000)`

| | Production | Beta |
|---|---|---|
| Version name | `0.1.3` | `0.1.2-beta` |
| Build number | `103000` | `102001`, `102002`... |
| Tag | `v0.1.3` | `v0.1.2-beta.1` |

The `-beta` suffix is controlled by `app.versionSuffix` in `gradle.properties` (default: `-beta`). Production releases override it to empty.

## Hotfix

1. `git checkout -b hotfix/v0.2.1 v0.2.0`
2. Fix and commit with conventional format
3. `./gradlew :app:release -Ptype=patch`
4. Merge back to `main`

## Promoting a Release

**Android** (internal → production):
```bash
bundle exec fastlane android promote from:internal to:production rollout:0.1
```

**iOS** (TestFlight → App Store):
```bash
bundle exec fastlane ios deploy_app_store
```

## Changelog

Changelogs are auto-generated from [conventional commits](https://www.conventionalcommits.org/) using [git-cliff](https://git-cliff.org/). Release commits (`release:`) are excluded.

## Signing & Secrets

See [release-signing-setup.md](../docs/release-signing-setup.md) for setup instructions and the full list of required GitHub secrets.
