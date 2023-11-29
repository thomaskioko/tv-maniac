plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.util)
                implementation(projects.data.category.api)
                implementation(projects.data.showimages.api)
                implementation(projects.data.shows.api)

                implementation(libs.voyager.core)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.kotlinx.collections)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))

                implementation(projects.data.showimages.testing)
                implementation(projects.data.shows.testing)

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
