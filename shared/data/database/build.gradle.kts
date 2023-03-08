plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.sqldelight)
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(libs.koin)
            implementation(libs.squareup.sqldelight.primitive.adapters)
        }

        sourceSets["androidMain"].dependencies {
            implementation(libs.squareup.sqldelight.driver.android)
            implementation(libs.hilt.android)
            configurations["kapt"].dependencies.add(
                org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                    "com.google.dagger",
                    "hilt-android-compiler",
                    libs.versions.dagger.get()
                )
            )

        }

        sourceSets["androidTest"].dependencies {
            implementation(kotlin("test"))
            implementation(libs.squareup.sqldelight.driver.jvm)
        }


        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
            implementation(libs.testing.kotest.assertions)
        }

        sourceSets["iosMain"].dependencies {
            implementation(libs.koin)
            implementation(libs.sqldelight.driver.native)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.core.db"
}

sqldelight {
    databases {
        create("TvManiacDatabase") {
            packageName.set("com.thomaskioko.tvmaniac.core.db")
        }
    }
    linkSqlite.set(true)
}
