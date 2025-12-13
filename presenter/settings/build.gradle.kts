plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
    useKotlinInject()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.view)

                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.data.datastore.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.domain.logout)
                implementation(projects.domain.user)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)

                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.data.datastore.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.user.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.i18n.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
