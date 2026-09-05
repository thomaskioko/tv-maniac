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
                api(projects.data.shows.api)
                api(projects.domain.showdetails)
                api(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.tasks.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.backup.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.shows.testing)
                implementation(projects.data.watchproviders.testing)
            }
        }
    }
}
