import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.tmdb.testing"
}

dependencies {

    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:domain:tmdb:api"))
    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(libs.coroutines.core)
}