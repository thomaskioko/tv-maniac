
import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("plugin.serialization") version ("1.6.10")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

version = libs.versions.shared.module.version.get()

android {
    namespace = "com.thomaskioko.tvmaniac.shared"
}

kotlin {

    val xcf = XCFramework()
    ios {
        binaries.framework {
            baseName = "TvManiac"
            xcf.add(this)
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries.withType<Framework> {
            isStatic = false
            linkerOpts.add("-lsqlite3")

            export(project(":shared:core:ui"))
            export(project(":shared:core:util"))
            export(project(":shared:core:database"))
            export(project(":shared:core:remote"))
            export(project(":shared:core:persistence"))
            export(project(":shared:domain:show-details:api"))
            export(project(":shared:domain:seasons:api"))
            export(project(":shared:domain:episodes:api"))
            export(project(":shared:domain:genre:api"))
            export(project(":shared:domain:last-air-episodes:api"))
            export(project(":shared:domain:similar:api"))
            export(project(":shared:domain:season-episodes:api"))
            export(project(":shared:domain:show-common:api"))
            export(project(":shared:domain:discover:api"))
            export(project(":shared:domain:trailers:api"))
            embedBitcode(BitcodeEmbeddingMode.BITCODE)

            transitiveExport = true
        }
    }
}

dependencies {
    commonMainApi(project(":shared:core:ui"))
    commonMainApi(project(":shared:core:util"))
    commonMainApi(project(":shared:core:database"))
    commonMainApi(project(":shared:core:remote"))
    commonMainApi(project(":shared:domain:show-details:api"))
    commonMainApi(project(":shared:domain:seasons:api"))
    commonMainApi(project(":shared:domain:episodes:api"))
    commonMainApi(project(":shared:domain:genre:api"))
    commonMainApi(project(":shared:domain:last-air-episodes:api"))
    commonMainApi(project(":shared:domain:similar:api"))
    commonMainApi(project(":shared:domain:season-episodes:api"))
    commonMainApi(project(":shared:domain:show-common:api"))
    commonMainApi(project(":shared:domain:discover:api"))
    commonMainApi(project(":shared:domain:trailers:api"))
    commonMainApi(project(":shared:core:persistence"))

    commonMainImplementation(project(":shared:domain:episodes:implementation"))
    commonMainImplementation(project(":shared:domain:show-details:implementation"))
    commonMainImplementation(project( ":shared:domain:seasons:implementation"))
    commonMainImplementation(project( ":shared:domain:genre:implementation"))
    commonMainImplementation(project( ":shared:domain:last-air-episodes:implementation"))
    commonMainImplementation(project(":shared:domain:similar:implementation"))
    commonMainImplementation(project(":shared:domain:season-episodes:implementation"))
    commonMainImplementation(project(":shared:domain:discover:implementation"))
    commonMainImplementation(project(":shared:domain:trailers:implementation"))

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
    outputDirectory(File("$projectDir/../../../", "tvmaniac-swift-packages"))
}
