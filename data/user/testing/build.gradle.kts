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
                api(projects.data.user.api)
                api(projects.data.user.implementation)
            }
        }
    }
}
