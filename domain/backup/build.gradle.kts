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
                api(projects.core.base)
                api(projects.core.view)
                api(projects.core.logger.api)
                api(projects.core.networkUtil.api)
                api(projects.core.tasks.api)
                api(projects.data.backup.api)
                api(projects.domain.showdetails)

                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.tasks.testing)
                implementation(projects.data.backup.testing)
            }
        }
    }
}
