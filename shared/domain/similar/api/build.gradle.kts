import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.similar.api"
}

dependencies {
    commonMainApi(projects.shared.core.util)
    commonMainApi(projects.shared.database)
    commonMainApi(projects.shared.domain.showCommon.api)
    commonMainApi(libs.kotlin.coroutines.core)
}
