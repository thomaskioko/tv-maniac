plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    addAndroidMultiplatformTarget(
        withDeviceTestBuilder = true,
    )
    explicitApi()
    useDependencyInjection()
}

kotlin {
    sourceSets {
        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.junit)
                implementation(libs.bundles.unittest)
                implementation(libs.androidx.runner)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.core.locale.api)
                implementation(projects.core.base)
                implementation(projects.data.datastore.api)
                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
