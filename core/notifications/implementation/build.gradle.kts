plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidMultiplatformTarget()
    useKotlinInject()
    useSerialization()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.core.ktx)
            }
        }

        commonMain {
            dependencies {
                api(projects.core.notifications.api)
                implementation(projects.core.base)
                implementation(libs.kotlinx.serialization.json)
                implementation(projects.core.logger.api)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}
