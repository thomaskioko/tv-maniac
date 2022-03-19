@file:Suppress("UnstableApiUsage")
import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.show_grid"
}

dependencies {
    implementation(projects.shared.domain.showDetails.api)
    implementation(projects.shared.domain.showCommon.api)
    implementation(libs.multiplatform.paging.core)
}
