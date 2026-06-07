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
                api(projects.data.accountManager.api)
                api(projects.data.traktauth.api)
                api(projects.data.traktauth.implementation)
            }
        }
    }
}
