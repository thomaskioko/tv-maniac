# API Keys & Configuration Setup

The app requires both TMDB and Trakt API credentials to function properly.

## 1. TMDB API Key

- [Create a TMDB API app](https://www.themoviedb.org/settings/api) and generate an API key

## 2. Trakt OAuth Credentials

- [Create a Trakt API app](https://trakt.tv/oauth/applications/new)
- Set the **Redirect URI** to: `tvmaniac://callback`
- Copy your **Client ID** and **Client Secret**

## 3. Add to local.properties

Create a `local.properties` file in the project root (if it doesn't exist) and add your credentials:

```properties
TMDB_API_KEY=your_tmdb_api_key_here
TRAKT_CLIENT_ID=your_trakt_client_id_here
TRAKT_CLIENT_SECRET=your_trakt_client_secret_here
TRAKT_REDIRECT_URI=tvmaniac://callback
```

**For CI/CD:** Set these as environment variables with the same names.

## 4. Firebase Crashlytics (Optional)

The app uses Firebase Crashlytics for crash reporting. It works without Firebase configured. Crash reporting will simply be disabled.

**Android:**
- Create a [Firebase project](https://console.firebase.google.com/) and register the Android app
- Download `google-services.json` and place it in the `app/` directory

**iOS:**
- Register the iOS app in the same Firebase project
- Download `GoogleService-Info.plist` and place it in `ios/ios/Resources/`

Both files are gitignored. **For CI/CD:** store them as base64-encoded secrets and decode them in your workflow before building.
