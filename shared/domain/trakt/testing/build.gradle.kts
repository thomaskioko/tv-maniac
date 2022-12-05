import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.trakt.testing"
}

dependencies {
    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:domain:trakt:api"))
    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(libs.coroutines.core)
}