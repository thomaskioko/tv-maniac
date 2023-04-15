plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["androidMain"].dependencies {
            implementation(libs.datetime)
        }

        sourceSets["commonMain"].dependencies {
            api(libs.ktor.serialization)
            implementation(libs.datetime)
            implementation(libs.kermit)
            implementation(libs.kotlinInject.runtime)
            implementation(libs.ktor.core)
            implementation(libs.coroutines.core)
            implementation(libs.yamlkt)
        }


        sourceSets["iosMain"].dependencies {
            implementation(libs.datetime)
        }

    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "com.thomaskioko.tvmaniac.core.util"
}