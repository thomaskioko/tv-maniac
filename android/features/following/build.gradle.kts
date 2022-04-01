import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.following"
}

dependencies {
    api(projects.shared.core.ui)
    api(projects.shared.core.util)
    api(projects.shared.domain.showDetails.api)
    api(projects.shared.domain.showCommon.api)

    implementation(projects.shared.database)
    implementation(projects.android.common.compose)

    implementation(libs.accompanist.insetsui)
}
