import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    id("tvmaniac.kmm.library")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.kapt")
    id("com.codingfeline.buildkonfig")
    alias(libs.plugins.serialization)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["androidMain"].dependencies {
            implementation(project(":shared:core:util"))
            implementation(project(":shared:data:network"))
            implementation(libs.appauth)
            implementation(libs.ktor.okhttp)
            implementation(libs.hilt.android)
            configurations["kapt"].dependencies.add(
                org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                    "com.google.dagger",
                    "hilt-android-compiler",
                    libs.versions.dagger.get()
                )
            )
        }


        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:network"))
            implementation(project(":shared:data:trakt-service:api"))
            implementation(libs.sqldelight.extensions)
            implementation(libs.koin)
            implementation(libs.ktor.core)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.serialization)
        }

        sourceSets["iosMain"].dependencies {
            implementation(project(":shared:data:network"))
            implementation(project(":shared:data:trakt-service:api"))
            implementation(libs.ktor.logging)
            implementation(libs.ktor.darwin)
        }

    }
}

android {
    namespace = "com.thomaskioko.trakt.service.implementation"
}

buildkonfig {
    packageName = "com.thomaskioko.trakt.service.implementation"

    defaultConfigs {
        buildConfigField(
            STRING,
            "TRAKT_CLIENT_ID",
            "\"" + propOrDef("TRAKT_CLIENT_ID", "") + "\""
        )
        buildConfigField(
            STRING,
            "TRAKT_CLIENT_SECRET",
            "\"" + propOrDef("TRAKT_CLIENT_SECRET", "") + "\""
        )
        buildConfigField(
            STRING,
            "TRAKT_REDIRECT_URI",
            "\"" + propOrDef("TRAKT_REDIRECT_URI", "") + "\""
        )
    }
}

fun <T : Any> propOrDef(propertyName: String, defaultValue: T): T {
    @Suppress("UNCHECKED_CAST")
    val propertyValue = project.properties[propertyName] as T?
    return propertyValue ?: defaultValue
}