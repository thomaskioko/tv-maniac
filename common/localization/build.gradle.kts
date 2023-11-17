plugins {
    id("plugin.tvmaniac.kotlin.android")
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.moko.resources)
}

kotlin {
    sourceSets {
        androidMain {
            dependsOn(commonMain.get())
        }

        commonMain {
            dependencies {
                api(libs.moko.core)
            }
        }

        iosMain {
            dependencies {
                dependsOn(commonMain.get())
            }
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.common.localization"
}

multiplatformResources {
    multiplatformResourcesPackage = "com.thomaskioko.tvmaniac.common.localization"
}

tasks.withType(com.android.build.gradle.tasks.MergeResources::class).configureEach {
    dependsOn(tasks.getByPath("generateMRandroidMain"))
}
tasks.withType(com.android.build.gradle.tasks.MapSourceSetPathsTask::class).configureEach {
    dependsOn(tasks.getByPath("generateMRandroidMain"))
}