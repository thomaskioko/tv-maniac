import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64
        else -> ::iosX64
    }

    iosTarget("ios") {}

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:core"))
            implementation(project(":shared:database"))
            implementation(project(":shared:remote"))
            implementation(project(":shared:domain:episodes:api"))
            implementation(project(":shared:domain:seasons:api"))

            implementation(libs.ktor.serialization)
            implementation(libs.koin.core)
            implementation(libs.kermit)
            implementation(libs.squareup.sqldelight.extensions)
            implementation(libs.kotlin.coroutines.core)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
            implementation(project(":shared:core-test"))

            implementation(libs.testing.ktor.mock)
            implementation(libs.testing.mockk.core)
            implementation(libs.testing.turbine)
            implementation(libs.testing.kotest.assertions)

            implementation(libs.testing.mockk.common)
        }

        sourceSets["androidMain"].dependencies {
        }

        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlinx.coroutines.FlowPreview")
            }
        }
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
