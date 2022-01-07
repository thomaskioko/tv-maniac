import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

kotlin {
    android()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64
        else -> ::iosX64
    }

    iosTarget("ios") {
    }

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(libs.koin.core)
            implementation(libs.squareup.sqldelight.runtime)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))

            implementation(libs.testing.kotest.assertions)
        }

        sourceSets["androidMain"].dependencies {
            implementation(libs.squareup.sqldelight.driver.android)
        }

        sourceSets["androidTest"].dependencies {
            implementation(kotlin("test"))
            implementation(libs.squareup.sqldelight.driver.jvm)
        }

        sourceSets["iosMain"].dependencies {
            implementation(libs.koin.core)
            implementation(libs.squareup.sqldelight.driver.native)
        }

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

sqldelight {
    database("TvManiacDatabase") {
        packageName = "com.thomaskioko.tvmaniac.datasource.cache"
        sourceFolders = listOf("sqldelight")
    }
}
