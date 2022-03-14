import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.episodes.api"
}

dependencies {
    commonMainApi(projects.shared.core)
    commonMainApi(projects.shared.database)
    commonMainApi(projects.shared.domain.seasons.api)
    commonMainApi(libs.kotlin.coroutines.core)
}
