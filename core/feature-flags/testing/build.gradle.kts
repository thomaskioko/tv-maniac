plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }

        commonMain.dependencies {
            api(projects.core.featureFlags.api)
        }
    }
}
