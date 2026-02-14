plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    useKotlinInject()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.util.api)
                implementation(projects.data.datastore.api)
                implementation(projects.data.upnext.api)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }
    }
}
