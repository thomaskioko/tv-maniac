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
                implementation(projects.features.root.nav)
                implementation(projects.features.showDetails.nav.api)
                implementation(projects.features.seasonDetails.nav.api)
                implementation(projects.features.episodeDetail.nav.api)
                implementation(projects.features.episodeDetail.presenter)
                implementation(projects.navigation.api)
            }
        }
    }
}
