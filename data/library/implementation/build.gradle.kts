plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)

                implementation(projects.core.base)
                implementation(projects.core.util.api)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.datastore.api)
                implementation(projects.data.library.api)

                implementation(libs.sqldelight.extensions)
            }
        }
    }
}
