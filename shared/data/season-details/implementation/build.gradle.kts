plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.ksp)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:util"))
            implementation(project(":shared:data:database"))
            implementation(project(":shared:data:datastore:api"))
            implementation(project(":shared:data:episodes:api"))
            implementation(project(":shared:data:season-details:api"))
            implementation(project(":shared:data:tmdb:api"))
            implementation(project(":shared:data:trakt-api:api"))

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
    add("kspAndroid", libs.kotlinInject.compiler)
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasondetails.implementation"
}