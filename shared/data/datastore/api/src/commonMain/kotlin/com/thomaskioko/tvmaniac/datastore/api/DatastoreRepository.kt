package com.thomaskioko.tvmaniac.datastore.api

import kotlinx.coroutines.flow.Flow

interface DatastoreRepository {
    fun saveTheme(theme: Theme)
    fun observeTheme(): Flow<Theme>
}

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}