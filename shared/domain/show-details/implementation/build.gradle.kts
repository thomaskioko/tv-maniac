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
            implementation(project( ":shared:domain:show-details:api"))
            implementation(project(":shared:domain:episodes:api"))
            implementation(project(":shared:domain:shows:api"))
            implementation(project(":shared:domain:similar:api"))
            implementation(project(":shared:domain:season-details:api"))
            implementation(project(":shared:domain:trailers:api"))
            implementation(project(":shared:domain:trakt:api"))

            implementation(libs.datetime)
            implementation(libs.sqldelight.extensions)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
            implementation(libs.testing.turbine)
            implementation(libs.testing.kotest.assertions)
            implementation(libs.testing.coroutines.test)
            implementation(libs.testing.mockk.common)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.details.implementation"
}
