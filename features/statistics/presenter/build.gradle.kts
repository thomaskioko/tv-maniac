plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.util.api)
                api(projects.core.view)
                api(projects.data.accountManager.api)
                api(projects.data.database.sqldelight)
                api(projects.data.episode.api)
                api(projects.data.subscription.api)
                api(projects.domain.statistics)
                api(projects.features.statistics.nav)
                api(projects.i18n.api)
                api(projects.i18n.generator)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)
                api(libs.kotlinx.datetime)

                implementation(projects.features.showDetails.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.base.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.ratings.testing)
                implementation(projects.data.rewatch.testing)
                implementation(projects.data.subscription.testing)
                implementation(projects.data.watchStatus.testing)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
