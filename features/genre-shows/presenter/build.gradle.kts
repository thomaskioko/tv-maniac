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
                implementation(projects.core.base)
                implementation(projects.navigation.api)
                implementation(projects.features.genreShows.nav)
            }
        }
    }
}
