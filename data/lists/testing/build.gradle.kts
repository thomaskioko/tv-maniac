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
                api(projects.data.lists.api)
                api(projects.data.lists.implementation)
                api(libs.coroutines.core)
            }
        }
    }
}
