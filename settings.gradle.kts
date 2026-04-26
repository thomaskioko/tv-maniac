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
        if (providers.gradleProperty("use.maven.local").orNull == "true") {
            mavenLocal()
        }
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
    id("com.gradle.develocity") version ("4.4.1")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        publishing.onlyIf { System.getenv("CI") != null }
    }
}

include(
    ":android-designsystem",
    ":features:calendar:ui",
    ":features:discover:ui",
    ":features:home:ui",
    ":features:root:presenter",
    ":features:root:ui",
    ":features:root:nav",
    ":features:progress:ui",
    ":features:search:ui",
    ":features:season-details:ui",
    ":features:show-details:ui",
    ":features:upnext:ui",
    ":features:watchlist:ui",
    ":features:debug:nav",
    ":features:debug:presenter",
    ":features:debug:ui",
    ":features:library:nav",
    ":features:library:presenter",
    ":features:library:ui",
    ":features:more-shows:nav",
    ":features:more-shows:presenter",
    ":features:more-shows:ui",
    ":features:profile:nav",
    ":features:profile:presenter",
    ":features:profile:ui",
    ":features:settings:nav",
    ":features:settings:presenter",
    ":features:settings:ui",
    ":features:trailers:presenter",
    ":features:trailers:ui",
    ":api:tmdb:api",
    ":api:tmdb:implementation",
    ":api:trakt:api",
    ":api:trakt:implementation",
    ":app",
    ":benchmark",
    ":core:appconfig:api",
    ":core:appconfig:implementation",
    ":core:base",
    ":core:connectivity:api",
    ":core:connectivity:implementation",
    ":core:connectivity:testing",
    ":core:imageloading:api",
    ":core:imageloading:implementation",
    ":core:locale:api",
    ":core:locale:implementation",
    ":core:locale:testing",
    ":core:logger:api",
    ":core:logger:implementation",
    ":core:logger:testing",
    ":core:network-util:api",
    ":core:network-util:implementation",
    ":core:network-util:testing",
    ":core:notifications:api",
    ":core:notifications:implementation",
    ":core:notifications:testing",
    ":core:paging",
    ":core:screenshot-tests",
    ":core:tasks:api",
    ":core:tasks:implementation",
    ":core:tasks:testing",
    ":core:util",
    ":core:util:api",
    ":core:util:implementation",
    ":core:util:testing",
    ":core:testing:di",
    ":core:view",
    ":data:calendar:api",
    ":data:calendar:implementation",
    ":data:calendar:testing",
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
    ":data:followedshows:api",
    ":data:followedshows:implementation",
    ":data:followedshows:testing",
    ":data:genre:api",
    ":data:genre:implementation",
    ":data:genre:testing",
    ":data:library:api",
    ":data:library:implementation",
    ":data:library:testing",
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
    ":data:sync-activity:api",
    ":data:sync-activity:implementation",
    ":data:sync-activity:testing",
    ":data:topratedshows:api",
    ":data:topratedshows:implementation",
    ":data:topratedshows:testing",
    ":data:trailers:api",
    ":data:trailers:implementation",
    ":data:trailers:testing",
    ":data:traktauth:api",
    ":data:traktauth:implementation",
    ":data:traktauth:testing",
    ":data:traktlists:api",
    ":data:traktlists:implementation",
    ":data:traktlists:testing",
    ":data:trendingshows:api",
    ":data:trendingshows:implementation",
    ":data:trendingshows:testing",
    ":data:upcomingshows:api",
    ":data:upcomingshows:implementation",
    ":data:upcomingshows:testing",
    ":data:upnext:api",
    ":data:upnext:implementation",
    ":data:upnext:testing",
    ":data:user:api",
    ":data:user:implementation",
    ":data:user:testing",
    ":data:watchlist:api",
    ":data:watchlist:implementation",
    ":data:watchlist:testing",
    ":data:watchproviders:api",
    ":data:watchproviders:implementation",
    ":data:watchproviders:testing",
    ":domain:calendar",
    ":domain:discover",
    ":domain:episode",
    ":domain:upnext",
    ":domain:followedshows",
    ":domain:genre",
    ":domain:library",
    ":domain:logout",
    ":domain:notifications",
    ":domain:recommendedshows",
    ":domain:settings",
    ":domain:seasondetails",
    ":domain:showdetails",
    ":domain:similarshows",
    ":domain:theme",
    ":domain:traktlists",
    ":domain:user",
    ":domain:watchlist",
    ":domain:watchproviders",
    ":i18n:api",
    ":i18n:generator",
    ":i18n:implementation",
    ":i18n:testing",
    ":navigation:api",
    ":navigation:implementation",
    ":navigation:testing",
    ":navigation:ui",
    ":features:calendar:presenter",
    ":features:calendar:nav",
    ":features:discover:presenter",
    ":features:discover:nav",
    ":features:episode-sheet:presenter",
    ":features:episode-sheet:nav",
    ":features:episode-sheet:ui",
    ":features:genre-shows:nav",
    ":features:genre-shows:presenter",
    ":features:home:nav",
    ":features:home:presenter",
    ":features:progress:presenter",
    ":features:progress:nav",
    ":features:trailers:nav",
    ":features:search:presenter",
    ":features:search:nav",
    ":features:season-details:presenter",
    ":features:season-details:nav",
    ":features:show-details:presenter",
    ":features:show-details:nav",
    ":features:upnext:presenter",
    ":features:upnext:nav",
    ":features:watchlist:presenter",
    ":features:watchlist:nav",
    ":ios-framework",
)
