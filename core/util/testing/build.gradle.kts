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
                api(projects.core.appconfig.api)
                api(projects.core.util.api)
                implementation(projects.core.base)
                implementation(projects.core.util.implementation)
            }
        }
    }
}
