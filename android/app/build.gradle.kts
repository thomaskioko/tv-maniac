import util.libs

plugins {
    `android-app-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac"
}

dependencies {
    implementation(project(":shared:shared"))

    implementation(projects.android.common.compose)
    implementation(projects.android.common.navigation)
    implementation(projects.android.features.discover)
    implementation(projects.android.features.home)
    implementation(projects.android.features.search)
    implementation(projects.android.features.showDetails)
    implementation(projects.android.features.showsGrid)
    implementation(projects.android.features.following)
    implementation(projects.android.features.settings)
    implementation(projects.android.features.seasons)

    implementation(libs.androidx.compose.activity)
    implementation(libs.accompanist.systemuicontroller)
}
