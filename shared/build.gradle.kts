import co.touchlab.skie.configuration.FlowInterop
import com.thomaskioko.tvmaniac.plugins.addKspDependencyForAllTargets
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  alias(libs.plugins.tvmaniac.android.library)
  alias(libs.plugins.tvmaniac.multiplatform)
  alias(libs.plugins.skie)
  alias(libs.plugins.ksp)
}

version = libs.versions.shared.module.version.get()

kotlin {
  sourceSets {
    targets.withType<KotlinNativeTarget>().configureEach {
      binaries.withType<Framework> {
        baseName = "TvManiac"

        isStatic = !debuggable
        linkerOpts.add("-lsqlite3")
        freeCompilerArgs += "-Xadd-light-debug=enable"

        export(projects.navigation.api)
        export(projects.datastore.api)
        export(projects.presentation.discover)
        export(projects.presentation.home)
        export(projects.presentation.library)
        export(projects.presentation.moreShows)
        export(projects.presentation.search)
        export(projects.presentation.seasondetails)
        export(projects.presentation.settings)
        export(projects.presentation.showDetails)
        export(projects.presentation.trailers)

        export(libs.decompose.decompose)
        export(libs.essenty.lifecycle)
      }
    }
    commonMain {
      dependencies {
        api(projects.core.base)
        api(projects.core.networkUtil)
        api(projects.core.util)
        api(projects.core.logger)

        api(projects.data.cast.api)
        api(projects.data.cast.implementation)
        api(projects.data.episodes.api)
        api(projects.data.episodes.implementation)
        api(projects.data.featuredshows.api)
        api(projects.data.featuredshows.implementation)
        api(projects.data.library.api)
        api(projects.data.library.implementation)
        api(projects.data.popularshows.api)
        api(projects.data.popularshows.implementation)
        api(projects.data.recommendedshows.api)
        api(projects.data.recommendedshows.implementation)
        api(projects.data.requestManager.api)
        api(projects.data.requestManager.api)
        api(projects.data.requestManager.implementation)
        api(projects.data.seasondetails.api)
        api(projects.data.seasondetails.implementation)
        api(projects.data.search.api)
        api(projects.data.search.implementation)
        api(projects.data.seasons.api)
        api(projects.data.seasons.implementation)
        api(projects.data.showdetails.api)
        api(projects.data.showdetails.implementation)
        api(projects.data.shows.api)
        api(projects.data.shows.implementation)
        api(projects.data.similar.api)
        api(projects.data.similar.implementation)
        api(projects.data.topratedshows.api)
        api(projects.data.topratedshows.implementation)
        api(projects.data.trailers.api)
        api(projects.data.trailers.implementation)
        api(projects.data.trendingshows.api)
        api(projects.data.trendingshows.implementation)
        api(projects.data.upcomingshows.api)
        api(projects.data.upcomingshows.implementation)
        api(projects.data.watchproviders.api)
        api(projects.data.watchproviders.implementation)

        api(projects.database)
        api(projects.datastore.api)
        api(projects.datastore.implementation)
        api(projects.tmdbApi.api)
        api(projects.tmdbApi.implementation)
        api(projects.traktApi.api)
        api(projects.traktApi.implementation)
        api(projects.traktAuth.api)
        api(projects.traktAuth.implementation)

        api(projects.navigation.api)
        api(projects.navigation.implementation)

        api(projects.presentation.discover)
        api(projects.presentation.library)
        api(projects.presentation.home)
        api(projects.presentation.moreShows)
        api(projects.presentation.search)
        api(projects.presentation.seasondetails)
        api(projects.presentation.settings)
        api(projects.presentation.showDetails)
        api(projects.presentation.trailers)

        api(libs.decompose.decompose)
        api(libs.essenty.lifecycle)
      }
    }
  }
}

android { namespace = "com.thomaskioko.tvmaniac.shared" }

ksp { arg("me.tatarka.inject.generateCompanionExtensions", "true") }

addKspDependencyForAllTargets(libs.kotlinInject.compiler)

kotlin.sourceSets.all { languageSettings.optIn("kotlin.experimental.ExperimentalObjCName") }

skie { features { group { FlowInterop.Enabled(false) } } }
