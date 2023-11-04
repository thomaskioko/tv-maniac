plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.requestManager.api)

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinInject.runtime)
                implementation(libs.sqldelight.extensions)
            }
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}