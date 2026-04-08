plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useKotlinInject()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.connectivity.api)
        }
    }
}
