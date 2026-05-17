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
                api(projects.data.traktlists.api)
                api(projects.data.traktlists.implementation)
                api(libs.coroutines.core)
            }
        }
    }
}
