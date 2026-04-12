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
                implementation(projects.features.showDetails.nav.api)
                implementation(projects.features.moreShows.nav.api)
                implementation(projects.navigation.api)
            }
        }
    }
}
