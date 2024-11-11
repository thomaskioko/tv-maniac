enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
// https://docs.gradle.org/7.6/userguide/configuration_cache.html#config_cache:stable
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "tv-maniac"

pluginManagement {
  includeBuild("build-plugins")

  repositories {
    gradlePluginPortal()
    google {
      content {
        includeGroupByRegex(".*google.*")
        includeGroupByRegex(".*android.*")
      }
    }
    mavenCentral()
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
  id("com.gradle.develocity") version ("3.18.1")
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
  }
}

include(
  ":android:app",
  ":android:designsystem",
  ":android:ui:discover",
  ":android:ui:home",
  ":android:ui:library",
  ":android:ui:more-shows",
  ":android:screenshot-tests",
  ":android:ui:search",
  ":android:ui:season-details",
  ":android:ui:settings",
  ":android:ui:show-details",
  ":android:ui:trailers",
  ":android:resources",
  ":core:base",
  ":core:logger",
  ":core:network-util",
  ":core:paging",
  ":core:util",
  ":core:util:testing",
  ":database",
  ":database:test",
  ":datastore:api",
  ":datastore:implementation",
  ":datastore:testing",
  ":data:cast:api",
  ":data:cast:implementation",
  ":data:cast:testing",
  ":data:episodes:api",
  ":data:episodes:implementation",
  ":data:episodes:testing",
  ":data:featuredshows:api",
  ":data:featuredshows:implementation",
  ":data:featuredshows:testing",
  ":data:library:api",
  ":data:library:implementation",
  ":data:library:testing",
  ":data:popularshows:api",
  ":data:popularshows:implementation",
  ":data:popularshows:testing",
  ":data:request-manager:api",
  ":data:request-manager:implementation",
  ":data:recommendedshows:api",
  ":data:recommendedshows:implementation",
  ":data:recommendedshows:testing",
  ":data:search:api",
  ":data:search:implementation",
  ":data:search:testing",
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
  ":data:showdetails:testing",
  ":data:similar:api",
  ":data:similar:implementation",
  ":data:similar:testing",
  ":data:trendingshows:api",
  ":data:trendingshows:implementation",
  ":data:trendingshows:testing",
  ":data:topratedshows:api",
  ":data:topratedshows:implementation",
  ":data:topratedshows:testing",
  ":data:trailers:api",
  ":data:trailers:implementation",
  ":data:trailers:testing",
  ":data:upcomingshows:api",
  ":data:upcomingshows:implementation",
  ":data:upcomingshows:testing",
  ":data:watchproviders:api",
  ":data:watchproviders:implementation",
  ":data:watchproviders:testing",
  ":navigation:api",
  ":navigation:implementation",
  ":presenter:discover",
  ":presenter:home",
  ":presenter:library",
  ":presenter:more-shows",
  ":presenter:search",
  ":presenter:seasondetails",
  ":presenter:settings",
  ":presenter:show-details",
  ":presenter:trailers",
  ":shared",
  ":tmdb-api:api",
  ":tmdb-api:implementation",
  ":trakt-api:api",
  ":trakt-api:implementation",
  ":trakt-auth:api",
  ":trakt-auth:implementation",
  ":trakt-auth:testing",
)
