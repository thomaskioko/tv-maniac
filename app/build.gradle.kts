plugins {
    `android-app-plugin`
    kotlin("plugin.serialization") version("1.5.20")
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":app-common:core"))
    implementation(project(":app-common:compose"))
    implementation(project(":app-common:navigation"))
    implementation(project(":app-features:discover"))
    implementation(project(":app-features:home"))
    implementation(project(":app-features:search"))
    implementation(project(":app-features:show-details"))
    implementation(project(":app-features:shows-grid"))
    implementation(project(":app-features:watchlist"))
}