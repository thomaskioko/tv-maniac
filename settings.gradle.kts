enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
    ":app-common:resources",
    ":app-features:discover",
    ":app-features:home",
    ":app-features:search",
    ":app-features:show-details",
    ":app-features:following",
    ":app-features:shows-grid",
    ":app-features:settings",
    ":app-features:seasons",
    ":shared",
    ":shared:core",
    ":shared:core-test",
    ":shared:database",
    ":shared:remote",
    ":shared:domain:show:api",
    ":shared:domain:show:implementation",
    ":shared:domain:seasons:api",
    ":shared:domain:seasons:implementation",
    ":shared:domain:episodes:api",
    ":shared:domain:episodes:implementation",
    ":shared:domain:genre:api",
    ":shared:domain:genre:implementation",
    ":shared:interactors",
    ":shared:domain:last-air-episodes:api",
    ":shared:domain:last-air-episodes:implementation",
    ":shared:domain:similar:api",
    ":shared:domain:similar:implementation",
    ":shared:domain:season-episodes:api",
    ":shared:domain:season-episodes:implementation",
    ":shared:domain:show-common:api",
)
