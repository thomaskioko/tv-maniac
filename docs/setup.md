# Configuration Setup

TMDB and Trakt API credentials are required.

## 1. TMDB API Key
[Create a TMDB app](https://www.themoviedb.org/settings/api) to generate a key.

## 2. Trakt OAuth Credentials
[Create a Trakt app](https://trakt.tv/oauth/applications/new).
- **Redirect URI**: `tvmaniac://callback`
- Note the **Client ID** and **Client Secret**.

## 3. Local Configuration
Create `local.properties` in the project root:

```properties
TMDB_API_KEY=your_tmdb_api_key
TRAKT_CLIENT_ID=your_trakt_client_id
TRAKT_CLIENT_SECRET=your_trakt_client_secret
TRAKT_REDIRECT_URI=tvmaniac://callback
```

**CI/CD**: Use identical environment variable names.

## 4. Firebase Crashlytics (Optional)
Crash reporting is disabled unless configured.

- **Android**: Place `google-services.json` in `app/`.
- **iOS**: Place `GoogleService-Info.plist` in `ios/ios/Resources/`.

Both files are gitignored. For CI/CD, store as base64-encoded secrets.
