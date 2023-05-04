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
    ":core:util",
    ":core:networkutil",
    ":core:database",
    ":core:datastore:api",
    ":core:datastore:implementation",
    ":core:datastore:testing",
    ":core:tmdb-api:api",
    ":core:tmdb-api:implementation",
    ":core:tmdb-api:testing",
    ":core:trakt-api:api",
    ":core:trakt-api:implementation",
    ":data:category:api",
    ":data:category:implementation",
    ":data:episodes:api",
    ":data:episodes:implementation",
    ":data:episodes:testing",
    ":data:season-details:api",
    ":data:season-details:implementation",
    ":data:season-details:testing",
    ":data:similar:api",
    ":data:similar:implementation",
    ":data:similar:testing",
    ":data:shows:api",
    ":data:shows:implementation",
    ":data:shows:testing",
    ":data:trailers:api",
    ":data:trailers:implementation",
    ":data:trailers:testing",
    ":data:profile:api",
    ":data:profile:implementation",
    ":data:profile:testing",
    ":domain:discover",
    ":domain:following",
    ":domain:seasondetails",
    ":domain:settings",
    ":domain:show-details",
    ":domain:trailers",
)
