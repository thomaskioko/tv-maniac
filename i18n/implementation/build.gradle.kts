plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
    addAndroidTarget(
        libraryConfiguration = {
            lint {
                baseline = file("lint-baseline.xml")
                disable += "AppBundleLocaleChanges"
            }
            testOptions.unitTests.isIncludeAndroidResources = true
        },
    )
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
                implementation(projects.i18n.testing)
                implementation(libs.bundles.unittest)
            }
        }
    }
}
