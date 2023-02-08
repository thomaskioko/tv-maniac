import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    id("tvmaniac.kmm.impl")
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["androidMain"].dependencies {
            implementation(project(":shared:core:util"))
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
            implementation(project(":shared:domain:tmdb:api"))
            implementation(project(":shared:domain:trailers:api"))
            implementation(project(":shared:domain:shows:api"))

            implementation(libs.sqldelight.extensions)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.trailers.implementation"
}