plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    multiplatform {
        addAndroidTarget {
            testOptions.unitTests.isIncludeAndroidResources = true
        }
        useKotlinInjectAnvilCompiler()
    }
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(libs.moko.resources.compose)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.androidx.junit)
                implementation(libs.robolectric)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.locale.api)
                implementation(projects.i18n.api)

                implementation(libs.coroutines.core)

                api(libs.moko.resources)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.locale.testing)
                implementation(libs.bundles.unittest)
            }
        }
    }
}
