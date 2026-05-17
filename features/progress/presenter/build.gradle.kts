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
                api(projects.features.calendar.presenter)
                api(projects.features.progress.nav)
                api(projects.features.upnext.presenter)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.coroutines.core)

                implementation(projects.features.home.nav)
            }
        }
    }
}
