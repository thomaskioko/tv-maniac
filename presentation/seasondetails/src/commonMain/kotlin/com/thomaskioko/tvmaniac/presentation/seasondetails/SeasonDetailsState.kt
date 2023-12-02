package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetails
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

sealed interface SeasonDetailsState

data object Loading : SeasonDetailsState

data class SeasonDetailsLoaded(
    val showTitle: String = "",
    val selectedSeason: String? = "",
    val seasonDetailsList: PersistentList<SeasonDetails> = persistentListOf(),
    val errorMessage: String? = null,
) : SeasonDetailsState

data class LoadingError(val message: String? = null) : SeasonDetailsState
