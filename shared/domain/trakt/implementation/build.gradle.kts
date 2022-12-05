import org.jetbrains.kotlin.kapt3.base.Kapt.kapt
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("plugin.serialization") version ("1.6.10")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.codingfeline.buildkonfig")
}

android {
    namespace = "com.thomaskioko.tvmaniac.trakt.auth.implementation"
}

dependencies {

    androidMainImplementation(project(":shared:core:ui"))
    androidMainImplementation(project(":shared:core:util"))
    androidMainImplementation(project(":shared:core:network"))
    androidMainImplementation(libs.appauth)
    androidMainImplementation(libs.ktor.okhttp)
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    iosMainImplementation(project(":shared:core:network"))
    iosMainImplementation(project(":shared:domain:trakt:api"))
    iosMainImplementation(libs.ktor.logging)
    iosMainImplementation(libs.ktor.darwin)

    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:domain:trakt:api"))
    commonMainImplementation(project(":shared:domain:shows:api"))
    commonMainImplementation(libs.squareup.sqldelight.extensions)
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin)
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.ktor.negotiation)
    commonMainImplementation(libs.ktor.logging)
    commonMainImplementation(libs.ktor.serialization.json)
    commonMainImplementation(libs.ktor.serialization)

}

buildkonfig {
    packageName = "com.thomaskioko.tvmaniac.trakt.auth.implementation"

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