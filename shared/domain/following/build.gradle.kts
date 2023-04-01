plugins {
    id("tvmaniac.kmm.domain")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["androidMain"].dependencies {
            implementation(libs.flowredux)
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
            implementation(project(":shared:data:shows:api"))
        }

        sourceSets["commonTest"].dependencies {
            implementation(project(":shared:data:shows:testing"))
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.domain.following"
}
