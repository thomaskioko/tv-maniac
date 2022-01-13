plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:core"))
    commonMainImplementation(project(":shared:database"))
    commonMainImplementation(libs.kotlin.coroutines.core)
}
