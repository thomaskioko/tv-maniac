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
            implementation(project(":shared:data:tmdb:api"))
            implementation(project(":shared:data:similar:api"))
            implementation(project(":shared:data:trakt:api"))
            implementation(project(":shared:data:shows:api"))

            implementation(libs.sqldelight.extensions)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
            implementation(libs.testing.turbine)
            implementation(libs.testing.kotest.assertions)
            implementation(libs.testing.coroutines.test)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.similar.implementation"
}
