
import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode

plugins {
    id("tvmaniac.kmm.library")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
    id("com.google.devtools.ksp")
}

version = libs.versions.shared.module.version.get()

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "TvManiac"
            isStatic = false
            transitiveExport = true
            linkerOpts.add("-lsqlite3")

            embedBitcode(BitcodeEmbeddingMode.BITCODE)

            export(project(":shared:core:base"))
            export(project(":shared:core:networkutil"))
            export(project(":shared:data:database"))
            export(project(":shared:data:datastore:api"))
            export(project(":shared:data:episodes:api"))
            export(project(":shared:data:similar:api"))
            export(project(":shared:data:season-details:api"))
            export(project(":shared:data:category:api"))
            export(project(":shared:data:trailers:api"))
            export(project(":shared:data:shows:api"))
            export(project(":shared:data:tmdb:api"))
            export(project(":shared:data:profile:api"))
            export(project(":shared:domain:following"))
            export(project(":shared:domain:discover"))
            export(project(":shared:domain:seasondetails"))
            export(project(":shared:domain:settings"))
            export(project(":shared:domain:show-details"))
            export(project(":shared:domain:trailers"))
        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":shared:core:base"))
                api(project(":shared:core:networkutil"))
                api(project(":shared:data:category:api"))
                api(project(":shared:data:database"))
                api(project(":shared:data:datastore:api"))
                api(project(":shared:data:episodes:api"))
                api(project(":shared:data:similar:api"))
                api(project(":shared:data:season-details:api"))
                api(project(":shared:data:shows:api"))
                api(project(":shared:data:trailers:api"))
                api(project(":shared:data:tmdb:api"))
                api(project(":shared:data:profile:api"))
                api(project(":shared:data:trakt-api:api"))
                api(project(":shared:domain:discover"))
                api(project(":shared:domain:following"))
                api(project(":shared:domain:seasondetails"))
                api(project(":shared:domain:settings"))
                api(project(":shared:domain:show-details"))
                api(project(":shared:domain:trailers"))

                implementation(project(":shared:data:category:implementation"))
                implementation(project(":shared:data:datastore:implementation"))
                implementation(project(":shared:data:episodes:implementation"))
                implementation(project(":shared:data:profile:implementation"))
                implementation(project(":shared:data:similar:implementation"))
                implementation(project(":shared:data:season-details:implementation"))
                implementation(project(":shared:data:shows:implementation"))
                implementation(project(":shared:data:tmdb:implementation"))
                implementation(project(":shared:data:trailers:implementation"))
                implementation(project(":shared:data:trakt-api:implementation"))

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }
        val commonTest by getting
        val androidMain by getting
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
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
