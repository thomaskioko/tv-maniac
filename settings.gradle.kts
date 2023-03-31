enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("tooling")

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "tv-maniac"

include(
    ":android:app",
    ":android:core:designsystem",
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
    ":shared:core:util",
    ":shared:data:database",
    ":shared:data:network",
    ":shared:data:datastore:api",
    ":shared:data:datastore:implementation",
    ":shared:data:datastore:testing",
    ":shared:data:season-details:testing",
    ":shared:data:episodes:api",
    ":shared:data:episodes:implementation",
    ":shared:data:episodes:testing",
    ":shared:data:similar:api",
    ":shared:data:similar:implementation",
    ":shared:data:similar:testing",
    ":shared:data:season-details:api",
    ":shared:data:season-details:implementation",
    ":shared:data:category:api",
    ":shared:data:category:implementation",
    ":shared:data:trailers:api",
    ":shared:data:trailers:implementation",
    ":shared:data:trailers:testing",
    ":shared:data:tmdb:api",
    ":shared:data:tmdb:implementation",
    ":shared:data:tmdb:testing",
    ":shared:data:trakt:api",
    ":shared:data:trakt:implementation",
    ":shared:data:trakt:testing",
    ":shared:data:trakt-profile:api",
    ":shared:data:trakt-profile:implementation",
    ":shared:data:trakt-profile:testing",
    ":shared:data:trakt-service:api",
    ":shared:data:trakt-service:implementation",
    ":shared:domain:discover",
    ":shared:domain:following",
    ":shared:domain:seasondetails",
    ":shared:domain:settings",
    ":shared:domain:show-details",
    ":shared:domain:trailers",
)
