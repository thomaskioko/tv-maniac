
import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("plugin.serialization") version ("1.6.10")
}

android {
    namespace = "com.thomaskioko.tvmaniac.tmdb.api"
}

dependencies {
    commonMainApi(project(":shared:core:database"))

    commonMainImplementation(libs.ktor.serialization)
}