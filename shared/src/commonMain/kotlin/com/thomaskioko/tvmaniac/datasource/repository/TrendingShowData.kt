package com.thomaskioko.tvmaniac.datasource.repository

import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.presentation.model.TvShow

data class TrendingShowData(
    val category: ShowCategory,
    val shows: List<TvShow>
)