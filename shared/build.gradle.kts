import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin(Plugins.multiplatform)
    kotlin(Plugins.cocoapods)
    kotlin(Plugins.serialization) version ("1.5.10")
    id(Plugins.androidLibrary)
    id(Plugins.buildkonfig)
}

version = "1.0"

kotlin {
    android()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iosTarget("ios") {}

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "https://github.com/c0de-wizard/tv-maniac"
        ios.deploymentTarget = "14.1"
        frameworkName = "shared"
        podfile = project.file("../ios/Podfile")
    }

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(libs.kotlin.datetime)
            implementation(libs.ktor.core)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.serialization)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        sourceSets["androidMain"].dependencies {
            implementation(libs.ktor.android)
        }

        sourceSets["androidTest"].dependencies {}

        sourceSets["iosMain"].dependencies {
            implementation(libs.ktor.ios)
        }

        sourceSets["iosTest"].dependencies {}
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

buildkonfig {
    packageName = "com.thomaskioko.tvmaniac.shared"

    val props = gradleLocalProperties(rootDir)
    defaultConfigs {
        buildConfigField(STRING, "TMDB_API_KEY", props.getProperty("TMDB_API_KEY"))
        buildConfigField(STRING, "TMDB_API_URL", props.getProperty("TMDB_API_URL"))
    }
}