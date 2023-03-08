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
            implementation(project(":shared:data:episodes:api"))
            implementation(project(":shared:data:season-details:api"))
        }

        sourceSets["commonTest"].dependencies {
            implementation(project(":shared:data:episodes:testing"))
            implementation(project(":shared:data:season-details:testing"))
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.seasondetails"
}
