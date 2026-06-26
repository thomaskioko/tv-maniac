plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()

    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlinx.coroutines.InternalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.okio)
            }
        }

        commonMain {
            dependencies {
                api(projects.data.datastore.api)
                implementation(projects.core.base)
                implementation(projects.core.locale.api)
                implementation(projects.core.logger.api)
                implementation(projects.core.imageloading.api)

                api(libs.androidx.datastore.preference)
            }
        }

        commonTest { dependencies { implementation(libs.bundles.unittest) } }
    }
}
