import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasondetails"
}

dependencies {
    implementation(project(":shared:core:ui"))
    implementation(project(":shared:core:util"))
    implementation(project(":shared:domain:season-details:api"))
    implementation(project(":android:core:compose"))

    implementation(libs.snapper)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material.icons)
}
