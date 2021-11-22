enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "tv-maniac"

include(
    ":app",
    ":app-common:core",
    ":app-common:compose",
    ":app-common:navigation",
    ":app-features:discover",
    ":app-features:home",
    ":app-features:search",
    ":app-features:show-details",
    ":app-features:watchlist",
    ":app-features:shows-grid",
    ":shared"
)
