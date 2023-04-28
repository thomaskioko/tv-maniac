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
    ":app",
    ":android-core:designsystem",
    ":android-core:navigation",
    ":android-core:resources",
    ":android-core:workmanager",
    ":android-core:trakt-auth",
    ":android-features:discover",
    ":android-features:home",
    ":android-features:search",
    ":android-features:show-details",
    ":android-features:following",
    ":android-features:shows-grid",
    ":android-features:settings",
    ":android-features:season-details",
    ":android-features:trailers",
    ":android-features:profile",
    ":shared:base",
    ":shared:core:util",
    ":shared:core:networkutil",
    ":shared:core:database",
    ":shared:core:datastore:api",
    ":shared:core:datastore:implementation",
    ":shared:core:datastore:testing",
    ":shared:core:tmdb-api:api",
    ":shared:core:tmdb-api:implementation",
    ":shared:core:tmdb-api:testing",
    ":shared:core:trakt-api:api",
    ":shared:core:trakt-api:implementation",
    ":shared:data:category:api",
    ":shared:data:category:implementation",
    ":shared:data:episodes:api",
    ":shared:data:episodes:implementation",
    ":shared:data:episodes:testing",
    ":shared:data:season-details:api",
    ":shared:data:season-details:implementation",
    ":shared:data:season-details:testing",
    ":shared:data:similar:api",
    ":shared:data:similar:implementation",
    ":shared:data:similar:testing",
    ":shared:data:shows:api",
    ":shared:data:shows:implementation",
    ":shared:data:shows:testing",
    ":shared:data:trailers:api",
    ":shared:data:trailers:implementation",
    ":shared:data:trailers:testing",
    ":shared:data:profile:api",
    ":shared:data:profile:implementation",
    ":shared:data:profile:testing",
    ":shared:domain:discover",
    ":shared:domain:following",
    ":shared:domain:seasondetails",
    ":shared:domain:settings",
    ":shared:domain:show-details",
    ":shared:domain:trailers",
)
