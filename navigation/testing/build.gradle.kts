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
            api(libs.turbine)

            implementation(libs.decompose.decompose)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}
