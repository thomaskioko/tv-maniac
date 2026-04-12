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
                implementation(projects.features.seasonDetails.nav.api)
                implementation(projects.features.showDetails.nav.api)
                implementation(projects.features.showDetails.presenter)
                implementation(projects.navigation.api)
                implementation(projects.features.root.presenter)
            }
        }
    }
}
