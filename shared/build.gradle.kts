import Kmm_domain_plugin_gradle.Utils.getIosTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    `kmm-domain-plugin`
    kotlin("plugin.serialization") version ("1.6.10")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

version = libs.versions.shared.module.version.get()

kotlin {
    android()

    val iosTarget = getIosTarget()

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
            api(project(":shared:domain:discover:api"))
            api(project(":shared:domain:seasons:api"))
            api(project(":shared:domain:episodes:api"))
            api(project(":shared:domain:genre:api"))
            implementation(project(":shared:domain:episodes:implementation"))
            implementation(project(":shared:domain:discover:implementation"))
            implementation(project(":shared:domain:seasons:implementation"))
            implementation(project(":shared:domain:genre:implementation"))

            implementation(libs.koin.core)
            implementation(libs.kotlin.coroutines.core)
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries.withType<Framework> {
            isStatic = false
            linkerOpts.add("-lsqlite3")

            export(project(":shared:core"))
            export(project(":shared:database"))
            export(project(":shared:remote"))
            export(project(":shared:domain:discover:api"))
            export(project(":shared:domain:seasons:api"))
            export(project(":shared:domain:episodes:api"))
            export(project(":shared:domain:genre:api"))

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

    distributionMode { local() }
    outputDirectory(File("$projectDir/../../", "tvmaniac-swift-packages"))
}
