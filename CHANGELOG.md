# Changelog

All notable changes to this project will be documented in this file.

## [0.1.2] - 2026-03-25

### Bug Fixes

- **Android**: Fix SnackBar shown in error state.
- **auth**: Update Trakt token endpoint and client authentication method
- update Firebase distribution path to use release APK
- fix linting
### CI/CD

- Ignore iOS tests in watchlist module
### Features

- **i18n**: Add new strings for marking episodes
- **ios**: Configure background processing and fetch modes
- enable experimental R8 optimized resource shrinking
- enable mapping file upload for Firebase Crashlytics in release build
- Add query to get the latest season for followed shows
- Add episode notification scheduling tasks
- Add 'Auto' image quality setting and optimize image loading
- Add `season_numbers` to `TvShow` SQL queries
- Refactor theme implementation in settings
### Miscellaneous

- update release workflow to use release-specific secrets and add signing files to .gitignore
- add encrypted signing keys
