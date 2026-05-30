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
                api(projects.data.watchlistPrefs.api)
                api(projects.features.continueWatching.presenter)
                api(projects.features.startWatching.presenter)
                api(projects.features.myShows.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.coroutines.core)

                implementation(projects.features.home.nav)
            }
        }

        androidMain {
            dependencies {
                implementation(projects.i18n.generator)
            }
        }
    }
}
