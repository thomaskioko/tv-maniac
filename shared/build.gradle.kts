import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version ("1.6.10")
    id("com.android.library")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

version = libs.versions.shared.module.version.get()

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
            api(project(":shared:core"))
            api(project(":shared:database"))
            api(project(":shared:remote"))

            implementation(libs.kotlin.datetime)
            implementation(libs.ktor.serialization)
            implementation(libs.koin.core)
            implementation(libs.napier)
            implementation(libs.multiplatform.paging.core)
            implementation(libs.squareup.sqldelight.extensions)
            implementation(libs.kotlin.coroutines.core)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))

            implementation(libs.testing.ktor.mock)
            implementation(libs.testing.turbine)
            implementation(libs.testing.kotest.assertions)

            implementation(libs.testing.mockk.common)
        }

        sourceSets["androidMain"].dependencies {
            implementation(libs.squareup.sqldelight.driver.android)
        }

        sourceSets["androidTest"].dependencies {
            implementation(kotlin("test"))

            implementation(libs.testing.androidx.junit)
            implementation(libs.testing.mockk.core)
        }

        sourceSets["iosMain"].dependencies {
            implementation(libs.ktor.ios)
            implementation(libs.kotlin.coroutines.core)

            val coroutineCore = libs.kotlin.coroutines.core.get()

            @Suppress("UnstableApiUsage")
            implementation("${coroutineCore.module.group}:${coroutineCore.module.name}:${coroutineCore.versionConstraint.displayName}") {
                version {
                    strictly(libs.versions.coroutines.native.get())
                }
            }
        }

        sourceSets["iosTest"].dependencies {
            implementation(libs.testing.mockk.common)
        }

        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlinx.coroutines.FlowPreview")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries.withType<Framework> {
            isStatic = false
            linkerOpts.add("-lsqlite3")

            export(project(":shared:core"))
            export(project(":shared:database"))
            export(project(":shared:remote"))

            transitiveExport = true
        }
    }
}

multiplatformSwiftPackage {
    packageName("TvManiac")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }

    /**
     * Uncomment to create local build.
     distributionMode { local() }
     outputDirectory(File("$projectDir/../../", "tvmaniac-swift-packages"))
     **/
}
