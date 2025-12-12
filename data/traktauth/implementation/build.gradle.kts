plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidMultiplatformTarget()
    explicitApi()
    useKotlinInject()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.appauth)
                implementation(libs.coroutines.core)
                implementation(projects.data.traktauth.api)
                implementation(projects.core.buildconfig.api)

                implementation(libs.androidx.activity)
                implementation(libs.androidx.browser)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.datastore.preference)
                implementation(libs.androidx.work.runtime)
                implementation(projects.data.datastore.api)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.util.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.api.trakt.api)
                implementation(libs.ktor.auth)
                implementation(libs.kotlinx.datetime)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.multiplatformsettings.core)
                implementation(libs.multiplatformsettings.coroutines)
            }
        }
    }
}
