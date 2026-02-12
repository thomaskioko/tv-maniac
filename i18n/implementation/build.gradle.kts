plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
    addAndroidTarget(
        enableAndroidResources = true,
        withHostTestBuilder = true,
        lintConfiguration = {
            baseline = file("lint-baseline.xml")
            disable += "AppBundleLocaleChanges"
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

dependencies {
    "androidHostTestCompilationImplementation"(libs.androidx.junit)
    "androidHostTestCompilationImplementation"(libs.robolectric)
}
