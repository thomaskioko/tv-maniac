plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.data.datastore.api)
                api(projects.data.syncActivity.api)
                api(projects.data.traktauth.api)
                api(projects.data.user.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
