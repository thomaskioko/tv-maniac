plugins {
    id("tvmaniac.kmm.impl")
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["androidMain"].dependencies {
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
            implementation(project(":shared:data:datastore:api"))
            implementation(libs.coroutines.core)
            implementation(libs.androidx.datastore.preference)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))

            implementation(libs.testing.coroutines.test)
            implementation(libs.testing.kotest.assertions)
            implementation(libs.testing.turbine)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.datastore.implementation"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
