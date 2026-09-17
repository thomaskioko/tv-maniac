plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useSerialization()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.genre.api)
                api(projects.navigation.api)
            }
        }
    }
}
