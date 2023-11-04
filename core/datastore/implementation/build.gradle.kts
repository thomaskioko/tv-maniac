plugins {
    id("plugin.tvmaniac.android.library")
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.datastore.api)
                implementation(projects.core.util)

                api(libs.androidx.datastore.preference)

                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))

                implementation(libs.coroutines.test)
                implementation(libs.kotest.assertions)
                implementation(libs.turbine)
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
    namespace = "com.thomaskioko.tvmaniac.shared.domain.datastore.implementation"
}
