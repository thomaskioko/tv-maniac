plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    addAndroidTarget(
        enableAndroidResources = true,
        withHostTestBuilder = true,
        includeAndroidResources = true,
    )
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
            api(projects.core.integration.infra)

            api(libs.decompose.decompose)
            api(libs.kotlin.test)
            api(libs.kotest.assertions)
            api(libs.coroutines.test)
        }

        androidMain.dependencies {
            api(libs.androidx.compose.ui.test)
            api(libs.androidx.junit)
            api(libs.robolectric)
            api(libs.ktor.core)
            api(libs.ktor.mock)
        }
    }
}
