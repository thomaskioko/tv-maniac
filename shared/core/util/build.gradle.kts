plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.kapt)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["androidMain"].dependencies {
            implementation(libs.datetime)
            implementation(libs.hilt.android)
            configurations["kapt"].dependencies.add(
                org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                    "com.google.dagger",
                    "hilt-android-compiler",
                    libs.versions.dagger.get()
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

    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "com.thomaskioko.tvmaniac.core.util"
}