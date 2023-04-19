plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.ksp)
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.shared.core.datastore.api)
            implementation(projects.shared.core.util)

            api(libs.androidx.datastore.preference)

            implementation(libs.kotlinInject.runtime)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))

            implementation(libs.coroutines.test)
            implementation(libs.kotest.assertions)
            implementation(libs.turbine)
        }
    }
}


dependencies {
    add("kspAndroid", libs.kotlinInject.compiler)
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.datastore.implementation"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
