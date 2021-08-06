
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization") version ("1.5.20")
    id("com.android.library")
    id("com.codingfeline.buildkonfig")
    id("com.squareup.sqldelight")
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

        // Configure separate debug and release source sets.
        val commonDebug by sourceSets.creating {
            dependsOn(sourceSets["commonMain"])
        }

        val commonRelease by sourceSets.creating {
            dependsOn(sourceSets["commonMain"])
        }

        sourceSets["commonMain"].dependencies {
            implementation(libs.kotlin.datetime)
            implementation(libs.kotlin.coroutines.core)

            implementation(libs.ktor.core)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.serialization)

            implementation(libs.napier)
            implementation(libs.squareup.sqldelight.runtime)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))

            implementation(libs.testing.assertK)
            implementation(libs.testing.ktor.mock)
            implementation(libs.testing.opentest)
            implementation(libs.testing.turbine)
            implementation(libs.testing.kotest.assertions)

            implementation(libs.testing.mockk.common)
        }

        sourceSets["androidMain"].dependencies {
            implementation(libs.ktor.android)
            implementation(libs.squareup.sqldelight.driver.android)
        }

        sourceSets["androidTest"].dependencies {
            implementation(kotlin("test"))

            implementation(libs.testing.androidx.junit)
            implementation(libs.squareup.sqldelight.driver.jvm)

            implementation(libs.testing.mockk.core)
        }

        sourceSets["iosMain"].dependencies {
            implementation(libs.ktor.ios)
            implementation(libs.squareup.sqldelight.driver.native)
        }

        sourceSets["iosTest"].dependencies {
            implementation(libs.testing.mockk.common)
        }

        sourceSets.matching {
            it.name.endsWith("Test")
        }.configureEach {
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
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

buildkonfig {
    val properties = Properties()
    val secretsFile = file("secrets.properties")
    if (secretsFile.exists()) {
        properties.load(FileInputStream(secretsFile))
    }

    packageName = "com.thomaskioko.tvmaniac.shared"
    defaultConfigs {
        buildConfigField(STRING, "TMDB_API_KEY", properties["TMDB_API_KEY"] as String)
        buildConfigField(STRING, "TMDB_API_URL", properties["TMDB_API_URL"] as String)
    }
}

sqldelight {
    database("TvManiacDatabase") {
        packageName = "com.thomaskioko.tvmaniac.datasource.cache"
        sourceFolders = listOf("sqldelight")
    }
}