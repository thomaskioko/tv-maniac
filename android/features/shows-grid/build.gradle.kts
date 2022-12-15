@file:Suppress("UnstableApiUsage")
import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.show_grid"
}

dependencies {
    implementation(project(":shared:core:util"))
    implementation(project(":shared:core:database"))
    implementation(project(":shared:domain:shows:api"))
    implementation(project(":shared:domain:trakt:api"))
    implementation(project(":shared:domain:show-details:api"))
    implementation(project(":android:core:compose"))

    implementation(libs.androidx.compose.paging)
    implementation(libs.flowredux)
    implementation(libs.accompanist.insetsui)
}
