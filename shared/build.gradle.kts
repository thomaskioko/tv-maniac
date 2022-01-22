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

    val iosTarget = getIosTarget()

    iosTarget("ios") {
        binaries {
            framework {
                baseName = "TvManiac"
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
            export(project(":shared:domain:show:api"))
            export(project(":shared:domain:seasons:api"))
            export(project(":shared:domain:episodes:api"))
            export(project(":shared:domain:genre:api"))
            export(project(":shared:domain:last-air-episodes:api"))

            transitiveExport = true
        }
    }
}

dependencies {
    commonMainApi(project(":shared:core"))
    commonMainApi(project(":shared:database"))
    commonMainApi(project(":shared:remote"))
    commonMainApi(project(":shared:domain:show:api"))
    commonMainApi(project(":shared:domain:seasons:api"))
    commonMainApi(project(":shared:domain:episodes:api"))
    commonMainApi(project(":shared:domain:genre:api"))
    commonMainApi(project(":shared:domain:last-air-episodes:api"))

    commonMainImplementation(project(":shared:domain:episodes:implementation"))
    commonMainImplementation(project(":shared:domain:show:implementation"))
    commonMainImplementation(project(":shared:domain:seasons:implementation"))
    commonMainImplementation(project(":shared:domain:genre:implementation"))
    commonMainImplementation(project(":shared:domain:last-air-episodes:implementation"))

    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.kotlin.coroutines.core)
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
