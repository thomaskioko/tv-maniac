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
                implementation(projects.core.base)
                implementation(projects.core.util.api)
                implementation(projects.data.datastore.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
