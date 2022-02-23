@file:Suppress("UnstableApiUsage")

plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(projects.shared.domain.show.api)
    implementation(projects.shared.domain.similar.api)
}
