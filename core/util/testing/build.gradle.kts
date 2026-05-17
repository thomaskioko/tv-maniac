plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.appconfig.api)
                api(projects.core.util.api)
            }
        }

        androidMain {
            dependencies {
                api(libs.kotlinx.datetime)
            }
        }

        val jvmAndroidMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                api(libs.junit4)
            }
        }

        val jvmMain by getting {
            dependsOn(jvmAndroidMain)
        }

        val androidMain by getting {
            dependsOn(jvmAndroidMain)
        }
    }
}
