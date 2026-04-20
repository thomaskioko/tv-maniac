plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.data.requestManager.api)
            implementation(projects.data.requestManager.implementation)
            implementation(libs.coroutines.core)
        }
    }
}
