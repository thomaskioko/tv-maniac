@file:Suppress("UnstableApiUsage")

plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(project(":shared:domain:show:api"))
    implementation(project(":shared:domain:episodes:api"))
    implementation(project(":shared:domain:genre:api"))
    implementation(project(":shared:domain:seasons:api"))
}
