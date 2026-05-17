plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.navigation.api)

                implementation(projects.features.genreShows.nav)
            }
        }
    }
}
