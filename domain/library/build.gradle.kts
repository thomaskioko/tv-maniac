plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.library.api)
                implementation(projects.data.showdetails.api)
                implementation(projects.data.syncActivity.api)
                implementation(projects.data.watchproviders.api)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.library.testing)
            }
        }
    }
}
