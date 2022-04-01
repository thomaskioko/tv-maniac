@file:Suppress("UnstableApiUsage")
import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.show_grid"
}

dependencies {
    api(projects.shared.core.ui)
    api(projects.shared.core.util)
    api(projects.shared.domain.showDetails.api)

    implementation(projects.shared.database)
    implementation(projects.shared.domain.showCommon.api)
    implementation(projects.android.common.compose)

    api(libs.androidx.compose.paging)
    api(libs.multiplatform.paging.core)
    implementation(libs.accompanist.insetsui)
}
