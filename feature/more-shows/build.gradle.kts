plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.feature.moreshows"
}

dependencies {
    implementation(projects.data.category.api)
    implementation(projects.data.shows.api)
    implementation(projects.common.navigation)
    implementation(projects.common.voyagerutil)

    implementation(libs.androidx.compose.paging)
    implementation(libs.kotlinx.collections)
}
