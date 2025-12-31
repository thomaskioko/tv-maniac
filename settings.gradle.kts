enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
// https://docs.gradle.org/7.6/userguide/configuration_cache.html#config_cache:stable
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "tv-maniac"

pluginManagement {
    repositories {
        if (providers.gradleProperty("use.maven.local").orNull == "true") {
            mavenLocal()
        }

        mavenCentral()
        google {
            content {
                includeGroupByRegex(".*google.*")
                includeGroupByRegex(".*android.*")
            }
        }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex(".*google.*")
                includeGroupByRegex(".*android.*")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("com.gradle.develocity") version ("4.3")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
    }
}

include(
    ":android-designsystem",
    ":android-feature:discover",
    ":android-feature:home",
    ":android-feature:more-shows",
    ":android-feature:profile",
    ":android-feature:search",
    ":android-feature:season-details",
    ":android-feature:settings",
    ":android-feature:show-details",
    ":android-feature:trailers",
    ":android-feature:watchlist",
    ":api:tmdb:api",
    ":api:tmdb:implementation",
    ":api:trakt:api",
    ":api:trakt:implementation",
    ":app",
    ":benchmark",
    ":core:base",
    ":core:buildconfig:api",
    ":core:buildconfig:implementation",
    ":core:buildconfig:testing",
    ":core:imageloading:api",
    ":core:imageloading:implementation",
    ":core:locale:api",
    ":core:locale:implementation",
    ":core:locale:testing",
    ":core:logger:api",
    ":core:logger:implementation",
    ":core:logger:testing",
    ":core:network-util",
    ":core:paging",
    ":core:screenshot-tests",
    ":core:util",
    ":core:util:api",
    ":core:util:implementation",
    ":core:util:testing",
    ":core:testing:di",
    ":core:view",
    ":data:cast:api",
    ":data:cast:implementation",
    ":data:cast:testing",
    ":data:database:sqldelight",
    ":data:database:testing",
    ":data:datastore:api",
    ":data:datastore:implementation",
    ":data:datastore:testing",
    ":data:episode:api",
    ":data:episode:implementation",
    ":data:episode:testing",
    ":data:featuredshows:api",
    ":data:featuredshows:implementation",
    ":data:featuredshows:testing",
    ":data:genre:api",
    ":data:genre:implementation",
    ":data:genre:testing",
    ":data:popularshows:api",
    ":data:popularshows:implementation",
    ":data:popularshows:testing",
    ":data:recommendedshows:api",
    ":data:recommendedshows:implementation",
    ":data:recommendedshows:testing",
    ":data:request-manager:api",
    ":data:request-manager:implementation",
    ":data:request-manager:testing",
    ":data:search:api",
    ":data:search:implementation",
    ":data:search:testing",
    ":data:seasondetails:api",
    ":data:seasondetails:implementation",
    ":data:seasondetails:testing",
    ":data:seasons:api",
    ":data:seasons:implementation",
    ":data:seasons:testing",
    ":data:showdetails:api",
    ":data:showdetails:implementation",
    ":data:showdetails:testing",
    ":data:shows:api",
    ":data:shows:implementation",
    ":data:similar:api",
    ":data:similar:implementation",
    ":data:similar:testing",
    ":data:topratedshows:api",
    ":data:topratedshows:implementation",
    ":data:topratedshows:testing",
    ":data:trailers:api",
    ":data:trailers:implementation",
    ":data:trailers:testing",
    ":data:traktauth:api",
    ":data:traktauth:implementation",
    ":data:traktauth:testing",
    ":data:trendingshows:api",
    ":data:trendingshows:implementation",
    ":data:trendingshows:testing",
    ":data:upcomingshows:api",
    ":data:upcomingshows:implementation",
    ":data:upcomingshows:testing",
    ":data:user:api",
    ":data:user:implementation",
    ":data:user:testing",
    ":data:watchlist:api",
    ":data:watchlist:implementation",
    ":data:watchlist:testing",
    ":data:watchproviders:api",
    ":data:watchproviders:implementation",
    ":data:watchproviders:testing",
    ":domain:discover",
    ":domain:episode",
    ":domain:genre",
    ":domain:logout",
    ":domain:recommendedshows",
    ":domain:seasondetails",
    ":domain:showdetails",
    ":domain:similarshows",
    ":domain:user",
    ":domain:watchlist",
    ":domain:watchproviders",
    ":i18n:api",
    ":i18n:generator",
    ":i18n:implementation",
    ":i18n:testing",
    ":navigation:api",
    ":navigation:implementation",
    ":presenter:discover",
    ":presenter:home",
    ":presenter:more-shows",
    ":presenter:profile",
    ":presenter:search",
    ":presenter:seasondetails",
    ":presenter:settings",
    ":presenter:show-details",
    ":presenter:trailers",
    ":presenter:watchlist",
    ":shared",
)
