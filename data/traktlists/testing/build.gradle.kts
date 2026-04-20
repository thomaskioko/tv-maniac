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

                implementation(projects.core.base)
                implementation(projects.data.traktlists.implementation)
                implementation(libs.coroutines.core)
            }
        }
    }
}
