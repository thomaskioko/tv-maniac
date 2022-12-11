import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.trailers.testing"
}

dependencies {
    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:domain:trailers:api"))

    commonMainImplementation(libs.coroutines.core)
}