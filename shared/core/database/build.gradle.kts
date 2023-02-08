plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.sqldelight)
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

        sourceSets["commonMain"].dependencies {
            implementation(libs.koin)
            implementation(libs.squareup.sqldelight.primitive.adapters)
        }

        sourceSets["androidMain"].dependencies {
            implementation(libs.squareup.sqldelight.driver.android)
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
