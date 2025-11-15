plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidMultiplatformTarget()
    useKotlinInject()
    explicitApi()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.buildconfig.api)
        }

        androidMain.dependencies {
            implementation(libs.androidx.security.crypto)
            implementation(libs.coroutines.core)
        }

        iosMain.dependencies {
            implementation(libs.coroutines.core)
        }
    }
}
