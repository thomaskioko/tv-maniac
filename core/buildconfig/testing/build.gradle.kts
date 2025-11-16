plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.buildconfig.api)
        }
    }
}
