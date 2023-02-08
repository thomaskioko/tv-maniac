import org.jetbrains.kotlin.config.AnalysisFlags.optIn
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.serialization)
}



kotlin {
    android()
    ios()

    sourceSets.all {
        languageSettings.apply {
            optIn("kotlin.RequiresOptIn")
            optIn("kotlin.time.ExperimentalTime")
            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            optIn("kotlinx.coroutines.FlowPreview")
        }
    }

    sourceSets {
        sourceSets["androidMain"].dependencies {
            implementation(project(":shared:core:util"))
            api(libs.ktor.okhttp)
            api(libs.ktor.negotiation)
            api(libs.ktor.logging)
            implementation(libs.ktor.okhttp)
            implementation(libs.squareup.sqldelight.driver.android)
            implementation(libs.hilt.android)
            configurations["kapt"].dependencies.add(
                org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                    "com.google.dagger",
                    "hilt-android-compiler",
                    libs.versions.dagger.get().toString()
                )
            )
        }

        sourceSets["androidTest"].dependencies {

        }

        sourceSets["commonMain"].dependencies {
            implementation(libs.koin)
            implementation(libs.ktor.core)
            implementation(libs.ktor.logging)
            api(libs.ktor.serialization)
            api(libs.ktor.serialization.json)
            api(libs.kermit)
        }

        sourceSets["iosMain"].dependencies {
            api(libs.ktor.serialization.json)
            api(libs.kermit)
        }
    }

}

android {
    namespace = "com.thomaskioko.tvmaniac.remote"
}