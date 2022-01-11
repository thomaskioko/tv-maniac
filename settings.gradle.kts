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
    ":app-common:annotations",
    ":app-common:compose",
    ":app-common:navigation",
    ":app-features:discover",
    ":app-features:home",
    ":app-features:search",
    ":app-features:show-details",
    ":app-features:watchlist",
    ":app-features:shows-grid",
    ":app-features:settings",
    ":shared",
    ":shared:core",
    ":shared:core-test",
    ":shared:database",
    ":shared:remote",
    ":shared:domain:discover:api",
    ":shared:domain:discover:implementation",
    ":shared:domain:seasons:api",
    ":shared:domain:seasons:implementation",
    ":shared:domain:episodes:api",
    ":shared:domain:episodes:implementation",
    ":shared:domain:genre:api",
    ":shared:domain:genre:implementation"
)
