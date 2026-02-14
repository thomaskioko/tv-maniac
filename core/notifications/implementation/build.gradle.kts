plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
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
                implementation(projects.core.util.api)
                implementation(libs.kotlinx.serialization.json)
                implementation(projects.core.logger.api)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}
