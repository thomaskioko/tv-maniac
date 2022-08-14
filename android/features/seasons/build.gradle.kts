import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasons"
}

dependencies {
    api(project(":shared:core:ui"))
    api(project(":shared:core:util"))
    api(project(":shared:domain:seasons:api"))
    api(project(":shared:domain:season-episodes:api"))
    implementation(project(":shared:domain:show-common:api"))
    implementation(project(":android:common:compose"))

    implementation(libs.snapper)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
