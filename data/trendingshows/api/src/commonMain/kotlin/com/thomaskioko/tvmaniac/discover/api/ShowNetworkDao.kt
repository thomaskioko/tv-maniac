package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.core.db.Show_networks

interface ShowNetworkDao {
    fun upsert(entity: Show_networks)
    fun upsert(entityList: List<Show_networks>)
}
