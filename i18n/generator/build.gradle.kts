plugins {
    alias(libs.plugins.tvmaniac.kmp)
    alias(libs.plugins.tvmaniac.resource.generator)
    alias(libs.plugins.moko.resources)
}

tvmaniac {
    multiplatform {
        explicitApi()
        addAndroidTarget {
            lint {
                baseline = file("lint-baseline.xml")
            }

            testOptions.unitTests.isIncludeAndroidResources = true
        }

        addIosTargetsWithXcFramework(
            frameworkName = "i18n",
        ) { framework ->
            with(framework) {
                isStatic = true

                export(libs.moko.resources)
            }
        }
    }
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
                api(libs.moko.resources)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.i18n.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}

multiplatformResources {
    resourcesPackage.set("com.thomaskioko.tvmaniac.i18n")
}
