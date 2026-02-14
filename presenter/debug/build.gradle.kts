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
                api(projects.core.view)

                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.util.api)
                implementation(projects.data.datastore.api)
                implementation(projects.domain.library)
                implementation(projects.domain.notifications)
                implementation(projects.domain.upnext)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }
    }
}
