plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    multiplatform {
        addAndroidMultiplatformTarget(
            withDeviceTestBuilder = true,
        )
        explicitApi()
        useKotlinInjectAnvilCompiler()
    }
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
