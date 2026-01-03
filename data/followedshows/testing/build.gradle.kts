plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.followedshows.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
