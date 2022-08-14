import util.libs

plugins {
    `android-app-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac"
}

dependencies {
    implementation(project(":shared:shared"))

    implementation(projects.android.core.compose)
    implementation(projects.android.core.navigation)
    implementation(projects.android.features.discover)
    implementation(projects.android.features.home)
    implementation(projects.android.features.search)
    implementation(projects.android.features.showDetails)
    implementation(projects.android.features.showsGrid)
    implementation(projects.android.features.following)
    implementation(projects.android.features.settings)
    implementation(projects.android.features.seasons)
    implementation(projects.android.features.videoPlayer)

    implementation(libs.androidx.compose.activity)
    implementation(libs.accompanist.systemuicontroller)
}
