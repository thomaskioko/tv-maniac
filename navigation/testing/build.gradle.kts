plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useSerialization()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.navigation.api)
            api(libs.coroutines.core)
            api(libs.decompose.decompose)
            api(libs.turbine)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}
