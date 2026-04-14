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
                implementation(projects.data.traktlists.api)
                implementation(projects.data.user.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
