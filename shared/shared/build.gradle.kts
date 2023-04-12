
import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id("tvmaniac.kmm.library")
    id("com.codingfeline.buildkonfig")
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

            embedBitcode(BitcodeEmbeddingMode.BITCODE)

            transitiveExport = true
        }
    }

    sourceSets {
        sourceSets["commonMain"].dependencies {

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

            implementation(libs.androidx.datastore.preference)
            implementation(libs.coroutines.core)
            implementation(libs.kotlinInject.runtime)
        }

        sourceSets["iosMain"].dependencies {
            implementation(libs.ktor.darwin)
            implementation(libs.sqldelight.driver.native)
        }
    }

}

android {
    namespace = "com.thomaskioko.tvmaniac.shared"
}

buildkonfig {
    packageName = "com.thomaskioko.tvmaniac.shared"

    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "TMDB_API_KEY",
            "\"" + propOrDef("TMDB_API_KEY", "") + "\""
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "TRAKT_CLIENT_ID",
            "\"" + propOrDef("TRAKT_CLIENT_ID", "") + "\""
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "TRAKT_CLIENT_SECRET",
            "\"" + propOrDef("TRAKT_CLIENT_SECRET", "") + "\""
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "TRAKT_REDIRECT_URI",
            "\"" + propOrDef("TRAKT_REDIRECT_URI", "") + "\""
        )
    }
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

fun <T : Any> propOrDef(propertyName: String, defaultValue: T): T {
    @Suppress("UNCHECKED_CAST")
    val propertyValue = project.properties[propertyName] as T?
    return propertyValue ?: defaultValue
}

