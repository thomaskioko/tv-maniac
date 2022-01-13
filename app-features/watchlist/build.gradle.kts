plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(project(":app-common:compose"))
    implementation(project(":shared:interactors"))
    implementation(project(":shared:domain:discover:api"))
}
