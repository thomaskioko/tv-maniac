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
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.data.accountManager.api)
                api(projects.data.lists.api)
                api(projects.data.showdetails.api)
                api(projects.data.user.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.lists.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.user.testing)
            }
        }
    }
}
