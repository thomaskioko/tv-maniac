plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainApi(project(":shared:core"))
    commonMainApi(project(":shared:database"))
    commonMainApi(project(":shared:domain:seasons:api"))
    commonMainApi(libs.kotlin.coroutines.core)
}
