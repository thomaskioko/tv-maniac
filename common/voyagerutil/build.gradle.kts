plugins {
    id("plugin.tvmaniac.android.library")
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(libs.voyager.navigator)
                implementation(libs.androidx.compose.runtime)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.presentation.discover)
                implementation(projects.presentation.library)
                implementation(projects.presentation.profile)
                implementation(projects.presentation.settings)
                implementation(projects.presentation.showDetails)
                implementation(projects.presentation.trailers)
                implementation(projects.presentation.seasondetails)

                implementation(libs.coroutines.core)
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