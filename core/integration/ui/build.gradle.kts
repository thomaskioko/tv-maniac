plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget(enableAndroidResources = true)
    android {
        useCompose()
        manifestPlaceholders(
            mapOf("appAuthRedirectScheme" to "com.thomaskioko.tvmaniac.test"),
        )
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.test)
            api(libs.coroutines.test)
        }

        androidMain.dependencies {
            api(libs.androidx.compose.ui.test)
            api(libs.androidx.compose.ui.test.common)
            compileOnly(libs.robolectric)
            implementation(libs.androidx.uiautomator)
            implementation("androidx.test:monitor:1.8.0")
            runtimeOnly(libs.kotlin.test.junit)
        }
    }
}
