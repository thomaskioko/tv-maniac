@file:Suppress("UnstableApiUsage")

import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.showdetails"
}

dependencies {
    api(project(":shared:core:ui"))
    api(project(":shared:core:util"))
    api(project(":shared:domain:show-details:api"))
    api(project(":shared:domain:similar:api"))
    api(project(":shared:domain:genre:api"))
    api(project(":shared:domain:seasons:api"))
    api(project(":shared:domain:show-common:api"))
    api(project(":shared:domain:last-air-episodes:api"))
    api(project(":shared:domain:trailers:api"))
    implementation(project(":android:core:compose"))

    implementation(libs.snapper)
    implementation(libs.accompanist.insetsui)
    implementation(libs.accompanist.navigation.material)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
