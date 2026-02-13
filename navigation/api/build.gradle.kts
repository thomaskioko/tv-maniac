plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useSerialization()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)

                implementation(projects.core.base)
                implementation(projects.data.datastore.api)
                implementation(projects.presenter.debug)
                implementation(projects.presenter.discover)
                implementation(projects.presenter.home)
                implementation(projects.presenter.moreShows)
                implementation(projects.presenter.profile)
                implementation(projects.presenter.search)
                implementation(projects.presenter.seasondetails)
                implementation(projects.presenter.settings)
                implementation(projects.presenter.showDetails)
                implementation(projects.presenter.trailers)

                implementation(libs.coroutines.core)
            }
        }
    }
}
