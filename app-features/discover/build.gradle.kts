import util.libs

plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(projects.shared.domain.show.api)
    implementation(projects.shared.domain.showCommon.api)
    implementation(libs.androidx.paging.runtime)
}
