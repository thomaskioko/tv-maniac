plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.connectivity.api)
            implementation(projects.core.base)
        }
    }
}
