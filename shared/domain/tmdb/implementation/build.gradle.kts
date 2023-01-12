
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("plugin.serialization") version ("1.6.10")
    kotlin("kapt")
    id("com.codingfeline.buildkonfig")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.tmdb.implementation"
}

dependencies {

    androidMainImplementation(project(":shared:core:network"))
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    iosMainImplementation(project(":shared:core:network"))
    iosMainImplementation(libs.ktor.negotiation)
    iosMainImplementation(libs.ktor.logging)
    iosMainImplementation(libs.ktor.darwin)

    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:domain:tmdb:api"))
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin)
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.ktor.negotiation)
    commonMainImplementation(libs.ktor.logging)
    commonMainImplementation(libs.ktor.serialization.json)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    commonTestImplementation(libs.ktor.serialization)

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
