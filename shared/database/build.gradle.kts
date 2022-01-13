plugins {
    `kmm-domain-plugin`
    id("com.squareup.sqldelight")
}

kotlin {
    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(libs.koin.core)
            implementation(libs.squareup.sqldelight.runtime)
        }

        sourceSets["commonTest"].dependencies {
            implementation(libs.testing.kotest.assertions)
        }

        sourceSets["androidMain"].dependencies {
            implementation(libs.squareup.sqldelight.driver.android)
        }

        sourceSets["androidTest"].dependencies {
            implementation(kotlin("test"))
            implementation(libs.squareup.sqldelight.driver.jvm)
        }

        sourceSets["iosMain"].dependencies {
            implementation(libs.koin.core)
            implementation(libs.squareup.sqldelight.driver.native)
        }
    }
}

sqldelight {
    database("TvManiacDatabase") {
        packageName = "com.thomaskioko.tvmaniac.datasource.cache"
        sourceFolders = listOf("sqldelight")
    }
}
