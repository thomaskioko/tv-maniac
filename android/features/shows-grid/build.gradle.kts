plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.show_grid"
}

dependencies {

    api(projects.shared.core.database)
    api(projects.shared.data.shows.api)

    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.paging)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.ui)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)
    api(libs.coroutines.core)

    implementation(projects.android.core.resources)
    implementation(projects.shared.core.networkutil)
    implementation(projects.shared.data.category.api)

    implementation(libs.androidx.paging.common)
    implementation(libs.flowredux)
}
