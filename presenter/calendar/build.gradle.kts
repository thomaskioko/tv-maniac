plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.view)
                implementation(projects.data.calendar.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.util.api)
                implementation(projects.domain.calendar)
                implementation(projects.i18n.api)
                implementation(projects.i18n.generator)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.calendar.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.i18n.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
