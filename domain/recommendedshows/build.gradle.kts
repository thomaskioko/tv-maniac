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
                api(projects.data.recommendedshows.api)
                api(libs.coroutines.core)
            }
        }
    }
}
