plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.data.requestManager.api)
            api(projects.data.requestManager.implementation)
        }
    }
}
