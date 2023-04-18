package com.thomaskioko.tvmaniac.datastore.api

import kotlinx.coroutines.flow.Flow

interface DatastoreRepository {
    fun saveTheme(theme: Theme)
    fun observeTheme(): Flow<Theme>
    fun saveSeasonId(id: Long)
    fun getSeasonId(): Flow<Long>
}

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}