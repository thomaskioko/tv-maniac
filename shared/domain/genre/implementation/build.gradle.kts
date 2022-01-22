plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:remote"))
    commonMainImplementation(project(":shared:domain:genre:api"))
    commonMainImplementation(libs.kermit)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.squareup.sqldelight.extensions)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core-test"))
}
