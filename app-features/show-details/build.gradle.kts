@file:Suppress("UnstableApiUsage")

plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(project(":shared:domain:show:api"))
    implementation(project(":shared:domain:similar:api"))
}
