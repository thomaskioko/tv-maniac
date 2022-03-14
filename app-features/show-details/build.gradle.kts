@file:Suppress("UnstableApiUsage")

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.showdetails"
}

dependencies {
    implementation(projects.shared.domain.showDetails.api)
    implementation(projects.shared.domain.similar.api)
}
