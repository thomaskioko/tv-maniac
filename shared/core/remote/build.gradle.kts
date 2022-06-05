
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
    namespace = "com.thomaskioko.tvmaniac.remote"
}

dependencies {
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.ktor.logging)
    commonMainImplementation(libs.ktor.serialization)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.kermit)

    androidMainImplementation(project(":shared:core:util"))

    androidMainImplementation(libs.ktor.android)
    androidMainImplementation(libs.squareup.sqldelight.driver.android)
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    iosMainImplementation(libs.ktor.ios)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core:test"))

    commonTestImplementation(libs.testing.ktor.mock)
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.mockk.common)

    androidTestImplementation(kotlin("test"))
    androidTestImplementation(libs.testing.androidx.junit)
}

buildkonfig {
    val properties = Properties()
    val secretsFile = file("../../../local.properties")
    if (secretsFile.exists()) {
        properties.load(FileInputStream(secretsFile))
    }

    packageName = "com.thomaskioko.tvmaniac.remote"
    defaultConfigs {
        buildConfigField(STRING, "TMDB_API_KEY", properties["TMDB_API_KEY"] as String)
    }
}
