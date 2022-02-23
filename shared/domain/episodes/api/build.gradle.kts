plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainApi(projects.shared.core)
    commonMainApi(projects.shared.database)
    commonMainApi(projects.shared.domain.seasons.api)
    commonMainApi(libs.kotlin.coroutines.core)
}
