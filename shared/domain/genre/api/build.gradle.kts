import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.genre.api"
}

dependencies {
    commonMainApi(project(":shared:core:util"))
    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(libs.kotlin.coroutines.core)
}
