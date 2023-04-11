plugins {
    id("tvmaniac.kmm.data")
    alias(libs.plugins.ksp)
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:episodes:api"))
            implementation(project(":shared:data:shows:api"))
            implementation(project(":shared:data:tmdb:api"))

            implementation(libs.kermit)
            implementation(libs.kotlinInject.runtime)
            implementation(libs.sqldelight.extensions)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
            implementation(libs.turbine)
            implementation(libs.kotest.assertions)
        }

    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.episodes.implementation"
}
