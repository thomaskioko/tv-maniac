import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.lastairepisodes.api"
}

dependencies {
    commonMainApi(project(":shared:core:database"))
    commonMainApi(project(":shared:core:util"))
    commonMainImplementation(libs.kotlin.coroutines.core)
}
