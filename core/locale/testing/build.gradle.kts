plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    explicitApi()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.locale.api)
                implementation(libs.coroutines.core)
            }
        }
    }
}
