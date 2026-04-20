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
                implementation(projects.core.locale.api)
                implementation(projects.core.locale.implementation)
                implementation(libs.coroutines.core)
            }
        }
    }
}
