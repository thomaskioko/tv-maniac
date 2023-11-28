plugins {
    id("plugin.tvmaniac.android.library")
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.compose.runtime)
            }
        }

        commonMain {
            dependencies {
                api(projects.presentation.discover)
                api(projects.presentation.library)

                api(libs.voyager.navigator)

                implementation(libs.kotlinInject.runtime)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.kotlinInject.compiler)
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.common.voyagerutil"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composecompiler.get()
    }
}