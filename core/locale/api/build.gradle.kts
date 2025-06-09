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
                implementation(libs.coroutines.core)
            }
        }
    }
}
