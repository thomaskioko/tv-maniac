plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useKotlinInject()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.util)
            implementation(projects.navigation.api)
            implementation(projects.navigation.implementation)
            implementation(projects.domain.watchlist)
            implementation(projects.presenter.home)
            implementation(projects.presenter.discover)
            implementation(projects.presenter.search)
            implementation(projects.presenter.settings)
            implementation(projects.presenter.watchlist)
            implementation(projects.presenter.showDetails)
            implementation(projects.presenter.moreShows)
            implementation(projects.presenter.seasondetails)
            implementation(projects.presenter.trailers)
            implementation(projects.data.datastore.api)
            implementation(projects.data.datastore.testing)
            implementation(projects.data.requestManager.api)
            implementation(projects.data.requestManager.testing)
            implementation(projects.domain.discover)
            implementation(projects.domain.showdetails)
            implementation(projects.domain.seasondetails)
            implementation(projects.data.traktauth.api)
            implementation(projects.data.traktauth.testing)

            api(libs.decompose.decompose)
            api(libs.kotlin.test)
            api(libs.kotest.assertions)
            api(libs.coroutines.test)
            api(libs.kotlinInject.runtime)
        }
    }
}
