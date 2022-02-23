import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.io.FileInputStream
import java.util.Properties

plugins {
    `kmm-domain-plugin`
    kotlin("plugin.serialization")
    id("com.codingfeline.buildkonfig")
}

dependencies {
    commonMainImplementation(libs.ktor.core)
    commonMainImplementation(libs.ktor.logging)
    commonMainImplementation(libs.ktor.serialization)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.kermit)

    androidMainImplementation(libs.ktor.android)
    androidMainImplementation(libs.squareup.sqldelight.driver.android)

    iosMainImplementation(libs.ktor.ios)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(projects.shared.coreTest)

    commonTestImplementation(libs.testing.ktor.mock)
    commonTestImplementation(libs.testing.turbine)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.mockk.common)

    androidTestImplementation(kotlin("test"))
    androidTestImplementation(libs.testing.androidx.junit)
}

buildkonfig {
    val properties = Properties()
    val secretsFile = file("../../local.properties")
    if (secretsFile.exists()) {
        properties.load(FileInputStream(secretsFile))
    }

    packageName = "com.thomaskioko.tvmaniac.remote"
    defaultConfigs {
        buildConfigField(STRING, "TMDB_API_KEY", properties["TMDB_API_KEY"] as String)
    }
}
