package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsDao
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface ShowDetailsComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideShowDetailsDao(bind: DefaultShowDetailsDao): ShowDetailsDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideShowDetailsRepository(bind: DefaultShowDetailsRepository): ShowDetailsRepository = bind
}
