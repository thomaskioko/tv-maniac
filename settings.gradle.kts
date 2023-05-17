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
    ":shared",
    ":core:util",
    ":core:networkutil",
    ":core:database",
    ":core:datastore:api",
    ":core:datastore:implementation",
    ":core:datastore:testing",
    ":data:watchlist:api",
    ":data:watchlist:implementation",
    ":data:watchlist:testing",
    ":core:tmdb-api:api",
    ":core:tmdb-api:implementation",
    ":core:trakt-api:api",
    ":core:trakt-api:implementation",
    ":data:category:api",
    ":data:category:implementation",
    ":data:episodeimages:api",
    ":data:episodeimages:implementation",
    ":data:episodeimages:testing",
    ":data:episodes:api",
    ":data:episodes:implementation",
    ":data:episodes:testing",
    ":data:seasons:api",
    ":data:seasons:implementation",
    ":data:seasons:testing",
    ":data:seasondetails:api",
    ":data:seasondetails:implementation",
    ":data:seasondetails:testing",
    ":data:similar:api",
    ":data:similar:implementation",
    ":data:similar:testing",
    ":data:showimages:api",
    ":data:showimages:implementation",
    ":data:showimages:testing",
    ":data:shows:api",
    ":data:shows:implementation",
    ":data:shows:testing",
    ":data:trailers:api",
    ":data:trailers:implementation",
    ":data:trailers:testing",
    ":data:profile:api",
    ":data:profile:implementation",
    ":data:profile:testing",
    ":presentation:discover",
    ":presentation:seasondetails",
    ":presentation:settings",
    ":presentation:show-details",
    ":presentation:trailers",
    ":presentation:watchlist",
)
