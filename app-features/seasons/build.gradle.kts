plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(project(":shared:domain:show:api"))
    implementation(project(":shared:domain:seasons:api"))
    implementation(project(":shared:domain:season-episodes:api"))
}
