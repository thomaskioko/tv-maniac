plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
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
