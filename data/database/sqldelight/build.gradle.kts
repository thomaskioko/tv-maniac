plugins {
    alias(libs.plugins.app.kmp)
    alias(libs.plugins.sqldelight)
}

scaffold {
    addAndroidMultiplatformTarget()
    useKotlinInject()
}

kotlin {
    sourceSets {
        androidMain { dependencies { implementation(libs.sqldelight.driver.android) } }

        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(libs.sqldelight.primitive.adapters)
                implementation(libs.kotlinx.datetime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.data.database.testing)

                implementation(libs.kotest.assertions)
                implementation(libs.kotlin.test)
            }
        }

        iosMain { dependencies { implementation(libs.sqldelight.driver.native) } }
    }
}

sqldelight {
    databases {
        create("TvManiacDatabase") {
            packageName = "com.thomaskioko.tvmaniac.db"

            schemaOutputDirectory.set(file("src/commonMain/sqldelight/com/thomaskioko/tvmaniac/schemas"))
            migrationOutputDirectory.set(file("src/commonMain/sqldelight/com/thomaskioko/tvmaniac/migrations"))
            verifyMigrations = true
        }
    }
}
