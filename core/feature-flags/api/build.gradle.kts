plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    useFeatureFlagCodegen()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(libs.kotlinx.datetime)
            }
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}
