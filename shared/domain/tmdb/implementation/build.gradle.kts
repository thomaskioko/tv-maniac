
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import util.libs
import java.io.FileInputStream
import java.util.*

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

    commonMainImplementation(project(":shared:domain:tmdb:api"))
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.koin)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core:test"))
    commonTestImplementation(libs.ktor.serialization)

    commonTestImplementation(libs.ktor.negotiation)
    commonTestImplementation(libs.ktor.serialization.json)
    commonTestImplementation(libs.testing.ktor.mock)
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.mockk.common)

}

buildkonfig {
    val properties = Properties()
    val secretsFile = file("../../../../local.properties")
    if (secretsFile.exists()) {
        properties.load(FileInputStream(secretsFile))
    }

    packageName = "com.thomaskioko.tvmaniac.tmdb.implementation"
    defaultConfigs {
        buildConfigField(STRING, "TMDB_API_KEY", properties["TMDB_API_KEY"] as String)
    }
}
