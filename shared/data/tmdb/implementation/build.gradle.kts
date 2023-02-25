
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    id("tvmaniac.kmm.impl")
    alias(libs.plugins.serialization)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["androidMain"].dependencies {
            implementation(project(":shared:data:network"))
            implementation(libs.hilt.android)
            configurations["kapt"].dependencies.add(
                org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                    "com.google.dagger",
                    "hilt-android-compiler",
                    libs.versions.dagger.get().toString()
                )
            )
        }

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:tmdb:api"))
            implementation(libs.ktor.core)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.serialization.json)
            implementation(libs.sqldelight.extensions)
        }

        sourceSets["commonTest"].dependencies {
            implementation(libs.ktor.serialization)
        }

        sourceSets["iosMain"].dependencies {
            implementation(project(":shared:data:network"))
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.darwin)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.tmdb.implementation"
}

buildkonfig {

    packageName = "com.thomaskioko.tvmaniac.tmdb.implementation"
    defaultConfigs {
        buildConfigField(
            STRING,
            "TMDB_API_KEY",
            "\"" + propOrDef("TMDB_API_KEY", "") + "\""
        )
    }
}

fun <T : Any> propOrDef(propertyName: String, defaultValue: T): T {
    @Suppress("UNCHECKED_CAST")
    val propertyValue = project.properties[propertyName] as T?
    return propertyValue ?: defaultValue
}
