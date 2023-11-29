plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}


kotlin {
    sourceSets {

        commonMain {
            dependencies {
                implementation(projects.data.trailers.api)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.kotlinx.collections)
                implementation(libs.voyager.core)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.data.trailers.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}