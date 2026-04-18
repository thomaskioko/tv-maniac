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
                implementation(projects.core.base)
                implementation(projects.navigation.api)
                implementation(projects.features.home.nav)
                implementation(projects.features.progress.nav)
                implementation(projects.features.calendar.presenter)
                implementation(projects.features.upnext.presenter)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.coroutines.core)
            }
        }
    }
}
