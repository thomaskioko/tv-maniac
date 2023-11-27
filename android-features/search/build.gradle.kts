plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.search"
}

dependencies {
    implementation(projects.common.navigation)
}
