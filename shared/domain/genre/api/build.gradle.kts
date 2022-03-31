import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.genre.api"
}

dependencies {
    commonMainApi(projects.shared.core.util)
    commonMainImplementation(projects.shared.database)
    commonMainImplementation(libs.kotlin.coroutines.core)
}
