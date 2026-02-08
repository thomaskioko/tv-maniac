plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidMultiplatformTarget()
    useKotlinInject()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.work.runtime)
            }
        }

        commonMain {
            dependencies {
                api(projects.core.tasks.api)
                implementation(libs.coroutines.core)
                implementation(projects.core.logger.api)
                implementation(libs.kotlinx.atomicfu)
            }
        }

        iosMain {
            dependencies {
                implementation(projects.core.base)
            }
        }
    }
}
