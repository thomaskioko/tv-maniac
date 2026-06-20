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

        val jvmAndroidMain =
            create("jvmAndroidMain") {
                dependsOn(commonMain.get())
                dependencies {
                    api(libs.junit4)
                }
            }

        jvmMain.get().dependsOn(jvmAndroidMain)
        androidMain.get().dependsOn(jvmAndroidMain)
    }
}
