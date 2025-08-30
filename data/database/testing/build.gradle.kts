plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidMultiplatformTarget()

    optIn(
        "kotlin.uuid.ExperimentalUuidApi",
    )
}

kotlin {
    sourceSets {
        androidMain { dependencies { implementation(libs.sqldelight.driver.jvm) } }

        commonMain {
            dependencies {
                api(projects.data.database.sqldelight)

                implementation(libs.kotlinx.datetime)

                implementation(libs.kotlin.test)
            }
        }

        iosMain { dependencies { implementation(libs.sqldelight.driver.native) } }

        jvmMain { dependencies { implementation(libs.sqldelight.driver.jvm) } }
    }
}
