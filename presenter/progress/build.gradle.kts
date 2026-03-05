plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.presenter.calendar)
                implementation(projects.presenter.upnext)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.coroutines.core)

                implementation(libs.kotlinInject.runtime)
            }
        }
    }
}
