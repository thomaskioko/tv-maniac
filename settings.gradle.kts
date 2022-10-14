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
    ":android:features:seasons",
    ":android:features:video-player",
    ":shared:shared",
    ":shared:core:ui",
    ":shared:core:util",
    ":shared:core:test",
    ":shared:core:persistence",
    ":shared:core:database",
    ":shared:core:network",
    ":shared:domain:show-details:api",
    ":shared:domain:show-details:implementation",
    ":shared:domain:seasons:api",
    ":shared:domain:seasons:implementation",
    ":shared:domain:episodes:api",
    ":shared:domain:episodes:implementation",
    ":shared:domain:genre:api",
    ":shared:domain:genre:implementation",
    ":shared:domain:last-air-episodes:api",
    ":shared:domain:last-air-episodes:implementation",
    ":shared:domain:similar:api",
    ":shared:domain:similar:implementation",
    ":shared:domain:season-episodes:api",
    ":shared:domain:season-episodes:implementation",
    ":shared:domain:shows:api",
    ":shared:domain:shows:implementation",
    ":shared:domain:trailers:api",
    ":shared:domain:trailers:implementation",
    ":shared:domain:tmdb:api",
    ":shared:domain:tmdb:implementation",
    ":shared:domain:trakt:api",
    ":shared:domain:trakt:implementation",
)
