plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    addAndroidTarget(
        enableAndroidResources = true,
        lintConfiguration = {
            baseline = file("lint-baseline.xml")
            disable += "AppBundleLocaleChanges"
        },
    )
}

kotlin {
    sourceSets {
        androidHostTest {
            dependencies {
                implementation(libs.androidx.test.core)
                implementation(projects.i18n.generator)
            }
        }

        androidMain {
            dependencies {
                api(projects.core.view)
                api(projects.i18n.generator)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(libs.moko.resources)
                api(projects.core.base)
                api(projects.core.locale.api)
                api(projects.i18n.api)

                implementation(projects.core.networkUtil.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.i18n.testing)
            }
        }

        jvmTest {
            dependencies {
                implementation(projects.i18n.generator)
            }
        }
    }
}

dependencies {
    "androidHostTestCompilationImplementation"(libs.androidx.junit)
    "androidHostTestCompilationImplementation"(libs.robolectric)
}
