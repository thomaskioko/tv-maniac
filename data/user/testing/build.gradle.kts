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
                implementation(projects.data.user.api)
                implementation(projects.data.user.implementation)
                implementation(projects.core.base)
            }
        }
    }
}
