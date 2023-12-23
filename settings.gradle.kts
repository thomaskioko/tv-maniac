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
    ":android:designsystem",
    ":android:resources",
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
    ":data:cast:api",
    ":data:cast:implementation",
    ":data:episodes:api",
    ":data:episodes:implementation",
    ":data:episodes:testing",
    ":data:featuredshows:api",
    ":data:featuredshows:implementation",
    ":data:library:api",
    ":data:library:implementation",
    ":data:library:testing",
    ":data:popularshows:api",
    ":data:popularshows:implementation",
    ":data:request-manager:api",
    ":data:request-manager:implementation",
    ":data:recommendedshows:api",
    ":data:recommendedshows:implementation",
    ":data:seasondetails:api",
    ":data:seasondetails:implementation",
    ":data:seasondetails:testing",
    ":data:seasons:api",
    ":data:seasons:implementation",
    ":data:seasons:testing",
    ":data:shows:api",
    ":data:shows:implementation",
    ":data:showdetails:api",
    ":data:showdetails:implementation",
    ":data:similar:api",
    ":data:similar:implementation",
    ":data:similar:testing",
    ":data:trendingshows:api",
    ":data:trendingshows:implementation",
    ":data:topratedshows:api",
    ":data:topratedshows:implementation",
    ":data:trailers:api",
    ":data:trailers:implementation",
    ":data:trailers:testing",
    ":data:upcomingshows:api",
    ":data:upcomingshows:implementation",
    ":data:watchproviders:api",
    ":data:watchproviders:implementation",
    ":feature:discover",
    ":feature:library",
    ":feature:more-shows",
    ":feature:search",
    ":feature:season-details",
    ":feature:settings",
    ":feature:show-details",
    ":feature:trailers",
    ":navigation",
    ":presentation:discover",
    ":presentation:library",
    ":presentation:more-shows",
    ":presentation:search",
    ":presentation:seasondetails",
    ":presentation:settings",
    ":presentation:show-details",
    ":presentation:trailers",
    ":shared",
)
