plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useSerialization()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)

                implementation(projects.core.base)
                implementation(projects.data.datastore.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
