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
                api(projects.core.view)
                api(projects.data.accountManager.api)
                api(projects.domain.lists)
                api(projects.features.lists.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.androidx.paging.common)
                api(libs.kotlinx.collections)

                implementation(projects.data.lists.api)
                implementation(projects.features.showDetails.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.lists.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.user.testing)
                implementation(projects.i18n.generator)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
