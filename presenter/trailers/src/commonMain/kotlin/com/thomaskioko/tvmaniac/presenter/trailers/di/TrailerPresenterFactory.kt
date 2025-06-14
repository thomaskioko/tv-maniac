package com.thomaskioko.tvmaniac.presenter.trailers.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface TrailersPresenterFactory {
    fun create(
        componentContext: ComponentContext,
        id: Long,
    ): TrailersPresenter
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, TrailersPresenterFactory::class)
class DefaultTrailersPresenterFactory(
    private val repository: TrailerRepository,
) : TrailersPresenterFactory {
    override fun create(
        componentContext: ComponentContext,
        id: Long,
    ): TrailersPresenter = TrailersPresenter(
        componentContext = componentContext,
        traktShowId = id,
        repository = repository,
    )
}
