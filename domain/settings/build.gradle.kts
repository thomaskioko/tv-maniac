plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.util.api)
                implementation(projects.domain.theme)
                implementation(projects.data.datastore.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
