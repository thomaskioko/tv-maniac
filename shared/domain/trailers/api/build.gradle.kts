import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.trailers.api"
}

dependencies {
    commonMainApi(project(":shared:core:util"))
    commonMainImplementation(project(":shared:core:ui"))
    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(libs.coroutines.core)
}
