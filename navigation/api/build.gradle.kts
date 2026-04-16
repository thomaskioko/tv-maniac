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
                api(libs.decompose.essenty.statekeeper)
                api(libs.essenty.lifecycle)

                implementation(projects.core.base)

                implementation(libs.coroutines.core)
            }
        }
    }
}
