import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.episodes.testing"
}

dependencies {
    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:domain:season-details:api"))

    commonMainImplementation(libs.coroutines.core)
}