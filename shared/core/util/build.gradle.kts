import org.jetbrains.kotlin.config.AnalysisFlags.optIn
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.kapt)
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
            implementation(libs.datetime)
            implementation(libs.hilt.android)
            configurations["kapt"].dependencies.add(
                org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                    "com.google.dagger",
                    "hilt-android-compiler",
                    libs.versions.dagger.get().toString()
                )
            )
        }

        sourceSets["commonMain"].dependencies {
            api(libs.ktor.serialization)
            implementation(libs.koin)
            implementation(libs.kermit)
            implementation(libs.ktor.core)
            implementation(libs.datetime)
            implementation(libs.coroutines.core)
        }


        sourceSets["iosMain"].dependencies {
            implementation(libs.koin)
            implementation(libs.datetime)
        }

        sourceSets["commonTest"].dependencies {}

        sourceSets["androidTest"].dependencies {}
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "com.thomaskioko.tvmaniac.core.util"
}