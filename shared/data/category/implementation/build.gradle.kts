plugins {
    id("tvmaniac.kmm.data")
    alias(libs.plugins.ksp)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:core:base"))
            implementation(project(":shared:data:database"))
            implementation(project(":shared:data:category:api"))

            implementation(libs.kotlinInject.runtime)
            implementation(libs.sqldelight.extensions)
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.category.implementation"
}