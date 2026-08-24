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
                api(projects.core.filestore.api)
            }
        }
    }
}
