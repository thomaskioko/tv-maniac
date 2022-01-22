plugins {
    `android-app-plugin`
}

dependencies {
    implementation(project(":shared:core"))
    implementation(project(":shared:database"))
    implementation(project(":shared:remote"))
    implementation(project(":shared:domain:show:api"))
    implementation(project(":shared:domain:show:implementation"))
    implementation(project(":shared:domain:seasons:api"))
    implementation(project(":shared:domain:seasons:implementation"))
    implementation(project(":shared:domain:episodes:api"))
    implementation(project(":shared:domain:episodes:implementation"))
    implementation(project(":shared:domain:genre:api"))
    implementation(project(":shared:domain:genre:implementation"))
    implementation(project(":shared:domain:last-air-episodes:api"))
    implementation(project(":shared:domain:last-air-episodes:implementation"))
    implementation(project(":shared:interactors"))
    implementation(project(":app-common:annotations"))
    implementation(project(":app-common:compose"))
    implementation(project(":app-common:resources"))
    implementation(project(":app-common:navigation"))
    implementation(project(":app-features:discover"))
    implementation(project(":app-features:home"))
    implementation(project(":app-features:search"))
    implementation(project(":app-features:show-details"))
    implementation(project(":app-features:shows-grid"))
    implementation(project(":app-features:following"))
    implementation(project(":app-features:settings"))
}
