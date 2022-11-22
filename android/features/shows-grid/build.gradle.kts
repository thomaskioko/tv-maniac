@file:Suppress("UnstableApiUsage")
import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.show_grid"
}

dependencies {
    api(project(":shared:core:ui"))
    api(project(":shared:core:util"))
    api(projects.shared.domain.showDetails.api)

    implementation(project(":shared:core:database"))
    implementation(project(":shared:domain:shows:api"))
    implementation(project(":shared:domain:trakt:api"))
    implementation(project(":android:core:compose"))

    api(libs.androidx.compose.paging)
    implementation(libs.accompanist.insetsui)
}
