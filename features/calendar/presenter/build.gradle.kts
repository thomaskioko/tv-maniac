plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.view)
                api(projects.core.logger.api)
                api(projects.data.traktauth.api)
                api(projects.domain.calendar)
                api(projects.features.progress.nav)
                api(projects.i18n.api)
                api(projects.i18n.generator)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.core.base)
                implementation(projects.core.util.api)
                implementation(projects.data.calendar.api)
                implementation(projects.features.episodeSheet.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(libs.kotlinx.datetime)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.calendar.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
