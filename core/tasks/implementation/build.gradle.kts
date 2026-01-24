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
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.data.datastore.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.domain.followedshows)
                implementation(projects.domain.user)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.kotlinx.datetime)
            }
        }

        iosMain {
            dependencies {
            }
        }
    }
}
