plugins {
    id("tvmaniac.kmm.data")
    alias(libs.plugins.ksp)
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:shows:api"))
            implementation(project(":shared:data:tmdb:api"))
            implementation(project(":shared:data:trailers:api"))

            implementation(libs.kermit)
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
    namespace = "com.thomaskioko.tvmaniac.data.trailers.implementation"
}