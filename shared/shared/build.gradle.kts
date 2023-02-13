
import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.serialization)
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

version = libs.versions.shared.module.version.get()

kotlin {
    android()

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

            export(project(":shared:core:util"))
            export(project(":shared:core:database"))
            export(project(":shared:core:network"))
            export(project(":shared:domain:show-details:api"))
            export(project(":shared:domain:episodes:api"))
            export(project(":shared:domain:settings:api"))
            export(project(":shared:domain:similar:api"))
            export(project(":shared:domain:season-details:api"))
            export(project(":shared:domain:shows:api"))
            export(project(":shared:domain:trailers:api"))
            export(project(":shared:domain:tmdb:api"))
            export(project(":shared:domain:trakt:api"))
            export(project(":shared:domain:following:api"))
            embedBitcode(BitcodeEmbeddingMode.BITCODE)

            transitiveExport = true
        }
    }

    sourceSets {
        sourceSets["commonMain"].dependencies {
            api(project(":shared:core:util"))
            api(project(":shared:core:database"))
            api(project(":shared:core:network"))
            api(project(":shared:domain:settings:api"))
            api(project(":shared:domain:show-details:api"))
            api(project(":shared:domain:episodes:api"))
            api(project(":shared:domain:similar:api"))
            api(project(":shared:domain:season-details:api"))
            api(project(":shared:domain:shows:api"))
            api(project(":shared:domain:trailers:api"))
            api(project(":shared:domain:tmdb:api"))
            api(project(":shared:domain:trakt:api"))
            api(project(":shared:domain:following:api"))

            implementation(project(":shared:domain:episodes:implementation"))
            implementation(project(":shared:domain:show-details:implementation"))
            implementation(project(":shared:domain:similar:implementation"))
            implementation(project(":shared:domain:season-details:implementation"))
            implementation(project(":shared:domain:trailers:implementation"))
            implementation(project(":shared:domain:tmdb:implementation"))
            implementation(project(":shared:domain:trakt:implementation"))
            implementation(project(":shared:domain:shows:implementation"))
            implementation(project(":shared:domain:settings:implementation"))

            implementation(libs.koin)
            implementation(libs.coroutines.core)
        }
    }
    
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared"
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
