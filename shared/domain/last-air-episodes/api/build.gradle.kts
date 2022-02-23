plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainApi(projects.shared.core)
    commonMainApi(projects.shared.database)
    commonMainApi(libs.kotlin.coroutines.core)
}
