@file:Suppress("UnstableApiUsage")

plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(project(":shared:domain:discover:api"))
    implementation(project(":shared:interactors"))
    implementation(libs.multiplatform.paging.core)
}
