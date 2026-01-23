plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.user.api)
                api(projects.data.traktauth.api)
                api(projects.data.requestManager.api)
                api(projects.core.util.api)

                implementation(projects.core.base)
                implementation(projects.data.datastore.api)
                implementation(projects.data.syncActivity.api)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }
    }
}
