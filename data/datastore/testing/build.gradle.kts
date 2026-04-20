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
                implementation(projects.data.datastore.api)
                implementation(projects.data.datastore.implementation)
                implementation(libs.coroutines.core)
            }
        }
    }
}
