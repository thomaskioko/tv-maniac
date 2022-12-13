enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "tv-maniac"

include(
    ":android:app",
    ":android:core:compose",
    ":android:core:navigation",
    ":android:core:resources",
    ":android:core:workmanager",
    ":android:core:trakt-auth",
    ":android:features:discover",
    ":android:features:home",
    ":android:features:search",
    ":android:features:show-details",
    ":android:features:following",
    ":android:features:shows-grid",
    ":android:features:settings",
    ":android:features:season-details",
    ":android:features:video-player",
    ":android:features:profile",
    ":shared:shared",
    ":shared:core:ui",
    ":shared:core:util",
    ":shared:core:test",
    ":shared:core:database",
    ":shared:core:network",
    ":shared:domain:show-details:api",
    ":shared:domain:show-details:implementation",
    ":shared:domain:episodes:api",
    ":shared:domain:episodes:implementation",
    ":shared:domain:settings",
    ":shared:domain:similar:api",
    ":shared:domain:similar:implementation",
    ":shared:domain:similar:testing",
    ":shared:domain:season-details:api",
    ":shared:domain:season-details:implementation",
    ":shared:domain:shows:api",
    ":shared:domain:shows:implementation",
    ":shared:domain:trailers:api",
    ":shared:domain:trailers:implementation",
    ":shared:domain:trailers:testing",
    ":shared:domain:tmdb:api",
    ":shared:domain:tmdb:implementation",
    ":shared:domain:tmdb:testing",
    ":shared:domain:trakt:api",
    ":shared:domain:trakt:implementation",
    ":shared:domain:trakt:testing",
    ":shared:domain:settings:api",
    ":shared:domain:settings:implementation",
    ":shared:domain:following:api",
)
