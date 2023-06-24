plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.ksp)
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["androidMain"].dependencies {
            implementation(projects.androidCore.workmanager)
            implementation(projects.core.traktAuth.api)
            implementation(projects.core.util)

            implementation(projects.core.util)
            implementation(libs.androidx.activity)
            implementation(libs.androidx.browser)
            implementation(libs.androidx.core)
        }

        sourceSets["commonMain"].dependencies {
            implementation(projects.core.traktAuth.api)
            implementation(projects.core.util)
            implementation(libs.kotlinInject.runtime)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.traktauth.implementation"
}

dependencies {
    add("kspAndroid", libs.kotlinInject.compiler)
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

