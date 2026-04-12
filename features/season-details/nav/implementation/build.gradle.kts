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
                implementation(projects.features.seasonDetails.nav.api)
                implementation(projects.features.seasonDetails.presenter)
                implementation(projects.navigation.api)
            }
        }
    }
}
