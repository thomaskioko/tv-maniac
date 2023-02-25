package com.thomaskioko.tvmaniac.datastore.testing

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.Theme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeDatastoreRepository : DatastoreRepository {

    private val themeFlow : Channel<Theme> = Channel(Channel.UNLIMITED)

    suspend fun setTheme(theme: Theme) {
        themeFlow.send(theme)
    }

    override fun saveTheme(theme: Theme) { }

    override fun observeTheme(): Flow<Theme> = themeFlow.receiveAsFlow()
}