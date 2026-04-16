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
                api(projects.features.episodeSheet.nav.api)
                implementation(projects.data.datastore.api)
            }
        }
    }
}
