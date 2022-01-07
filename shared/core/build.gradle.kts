import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget


plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

version = libs.versions.shared.module.version.get()

kotlin {
    android()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64
        else -> ::iosX64
    }

    iosTarget("ios") {
        binaries {
            framework {
                baseName = "TvManiac"
            }
        }
    }

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.koin.core)
        }

        sourceSets["iosMain"].dependencies {
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.koin.core)
        }

        val commonTest by getting
        val androidMain by getting
        val androidTest by getting
        val iosTest by getting
    }
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
