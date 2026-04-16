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
                api(libs.decompose.decompose)
                api(projects.navigation.api)
                api(projects.features.episodeSheet.nav.api)
                implementation(projects.data.datastore.api)
            }
        }
    }
}
