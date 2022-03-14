import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.details"
}

dependencies {
    implementation(projects.shared.domain.discover.api)
    implementation(projects.shared.domain.showCommon.api)
    implementation(libs.androidx.paging.runtime)
}
