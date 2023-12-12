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
    ":android-core:designsystem",
    ":android-core:resources",
    ":app",
    ":core:database",
    ":core:datastore:api",
    ":core:datastore:implementation",
    ":core:datastore:testing",
    ":core:tmdb-api:api",
    ":core:tmdb-api:implementation",
    ":core:trakt-api:api",
    ":core:trakt-api:implementation",
    ":core:trakt-auth:api",
    ":core:trakt-auth:implementation",
    ":core:trakt-auth:testing",
    ":core:util",
    ":data:category:api",
    ":data:category:implementation",
    ":data:episodeimages:api",
    ":data:episodeimages:implementation",
    ":data:episodeimages:testing",
    ":data:episodes:api",
    ":data:episodes:implementation",
    ":data:episodes:testing",
    ":data:library:api",
    ":data:library:implementation",
    ":data:library:testing",
    ":data:profile:api",
    ":data:profile:implementation",
    ":data:profile:testing",
    ":data:profilestats:api",
    ":data:profilestats:implementation",
    ":data:profilestats:testing",
    ":data:request-manager:api",
    ":data:request-manager:implementation",
    ":data:seasondetails:api",
    ":data:seasondetails:implementation",
    ":data:seasondetails:testing",
    ":data:seasons:api",
    ":data:seasons:implementation",
    ":data:seasons:testing",
    ":data:showimages:api",
    ":data:showimages:implementation",
    ":data:showimages:testing",
    ":data:shows:api",
    ":data:shows:implementation",
    ":data:shows:testing",
    ":data:similar:api",
    ":data:similar:implementation",
    ":data:similar:testing",
    ":data:trendingshows:api",
    ":data:trendingshows:implementation",
    ":data:trailers:api",
    ":data:trailers:implementation",
    ":data:trailers:testing",
    ":data:upcomingshows:api",
    ":data:upcomingshows:implementation",
    ":feature:discover",
    ":feature:library",
    ":feature:more-shows",
    ":feature:profile",
    ":feature:search",
    ":feature:season-details",
    ":feature:settings",
    ":feature:show-details",
    ":feature:trailers",
    ":navigation",
    ":presentation:discover",
    ":presentation:library",
    ":presentation:more-shows",
    ":presentation:profile",
    ":presentation:search",
    ":presentation:seasondetails",
    ":presentation:settings",
    ":presentation:show-details",
    ":presentation:trailers",
    ":shared",
)
