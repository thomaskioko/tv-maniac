plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useSerialization()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.networkUtil)
            }
        }
    }
}
