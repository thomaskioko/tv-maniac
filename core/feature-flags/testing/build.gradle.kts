plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            api(libs.kotlinx.datetime)
        }

        jvmMain.dependencies {
            api(libs.kotlinx.datetime)
        }

        commonMain.dependencies {
            api(projects.core.featureFlags.api)
        }
    }
}
