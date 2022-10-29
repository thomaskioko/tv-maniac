import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.similar.api"
}

dependencies {
    commonMainApi(project(":shared:core:util"))
    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(project(":shared:domain:shows:api"))

    commonMainImplementation(libs.coroutines.core)
}
