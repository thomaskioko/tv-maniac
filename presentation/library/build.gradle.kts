plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.library.api)

                implementation(libs.flowredux)
                implementation(libs.kotlinInject.runtime)
                implementation(libs.kotlinx.collections)
                implementation(libs.voyager.core)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.data.library.testing)

                implementation(libs.coroutines.test)
                implementation(libs.kotest.assertions)
                implementation(libs.turbine)
            }
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}
