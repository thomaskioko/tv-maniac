
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
    val iosTargets = listOf(iosX64(), iosArm64())
    iosTargets.forEach {
        it.binaries.framework {
            baseName = "TvManiac"
            isStatic = false
            linkerOpts.add("-lsqlite3")
            xcf.add(this)
        }
    }

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:core:util"))
            implementation(project(":shared:data:database"))
            implementation(project(":shared:data:datastore:api"))
            implementation(project(":shared:data:network"))
            implementation(project(":shared:data:episodes:api"))
            implementation(project(":shared:data:similar:api"))
            implementation(project(":shared:data:season-details:api"))
            implementation(project(":shared:data:category:api"))
            implementation(project(":shared:data:trailers:api"))
            implementation(project(":shared:data:tmdb:api"))
            implementation(project(":shared:data:trakt:api"))
            implementation(project(":shared:domain:discover"))
            implementation(project(":shared:domain:following"))
            implementation(project(":shared:domain:seasondetails"))
            implementation(project(":shared:domain:settings"))
            implementation(project(":shared:domain:show-details"))
            implementation(project(":shared:domain:trailers"))

            implementation(project(":shared:data:datastore:implementation"))
            implementation(project(":shared:data:episodes:implementation"))
            implementation(project(":shared:data:similar:implementation"))
            implementation(project(":shared:data:season-details:implementation"))
            implementation(project(":shared:data:trailers:implementation"))
            implementation(project(":shared:data:tmdb:implementation"))
            implementation(project(":shared:data:trakt:implementation"))
            implementation(project(":shared:data:category:implementation"))

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
