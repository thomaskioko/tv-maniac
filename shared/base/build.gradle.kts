
import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode

plugins {
    id("tvmaniac.kmm.library")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
    alias(libs.plugins.ksp)
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

            export(projects.shared.core.database)
            export(projects.shared.core.datastore.api)
            export(projects.shared.core.tmdbApi.api)
            export(projects.shared.data.episodes.api)
            export(projects.shared.data.similar.api)
            export(projects.shared.data.seasonDetails.api)
            export(projects.shared.data.category.api)
            export(projects.shared.data.trailers.api)
            export(projects.shared.data.shows.api)
            export(projects.shared.data.profile.api)
            export(projects.shared.domain.discover)
            export(projects.shared.domain.following)
            export(projects.shared.domain.seasondetails)
            export(projects.shared.domain.settings)
            export(projects.shared.domain.showDetails)
            export(projects.shared.domain.trailers)
        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.shared.core.database)
                api(projects.shared.core.datastore.api)
                api(projects.shared.core.networkutil)
                api(projects.shared.core.tmdbApi.api)
                api(projects.shared.core.traktApi.api)
                api(projects.shared.core.util)
                api(projects.shared.data.category.api)
                api(projects.shared.data.episodes.api)
                api(projects.shared.data.similar.api)
                api(projects.shared.data.seasonDetails.api)
                api(projects.shared.data.shows.api)
                api(projects.shared.data.trailers.api)
                api(projects.shared.data.profile.api)
                api(projects.shared.domain.discover)
                api(projects.shared.domain.following)
                api(projects.shared.domain.seasondetails)
                api(projects.shared.domain.settings)
                api(projects.shared.domain.seasondetails)
                api(projects.shared.domain.showDetails)
                api(projects.shared.domain.trailers)

                implementation(projects.shared.core.datastore.implementation)
                implementation(projects.shared.core.tmdbApi.implementation)
                implementation(projects.shared.core.traktApi.implementation)
                implementation(projects.shared.data.category.implementation)
                implementation(projects.shared.data.episodes.implementation)
                implementation(projects.shared.data.profile.implementation)
                implementation(projects.shared.data.similar.implementation)
                implementation(projects.shared.data.seasonDetails.implementation)
                implementation(projects.shared.data.shows.implementation)
                implementation(projects.shared.data.trailers.implementation)

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
    namespace = "com.thomaskioko.tvmaniac.shared.base"
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
