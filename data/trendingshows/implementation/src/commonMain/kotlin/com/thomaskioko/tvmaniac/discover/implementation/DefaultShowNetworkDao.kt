package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.core.db.Show_networks
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.discover.api.ShowNetworkDao
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultShowNetworkDao(
    database: TvManiacDatabase,
) : ShowNetworkDao {

    private val showNetworkQueries = database.show_networksQueries

    override fun upsert(entity: Show_networks) {
        showNetworkQueries.transaction {
            showNetworkQueries.upsert(
                show_id = entity.show_id,
                network_id = entity.network_id,
            )
        }
    }

    override fun upsert(entityList: List<Show_networks>) {
        entityList.forEach { upsert(it) }
    }
}
