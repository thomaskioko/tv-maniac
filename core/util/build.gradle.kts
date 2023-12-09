plugins {
    id("plugin.tvmaniac.kotlin.android")
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}


kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }

        commonMain {
            dependencies {
                api(libs.ktor.serialization)

                implementation(libs.coroutines.core)
                implementation(libs.decompose.decompose)
                implementation(libs.kermit)
                implementation(libs.napier)
                implementation(libs.kotlinInject.runtime)
                implementation(libs.ktor.core)
                implementation(libs.yamlkt)
            }
        }


        iosMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.util"

    sourceSets["main"].apply {
        resources.srcDirs("src/commonMain/resources") // <-- add the commonMain Resources
    }
}


dependencies {
    add("kspAndroid", libs.kotlinInject.compiler)
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}