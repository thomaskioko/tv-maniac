package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.core.db.Networks
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.discover.api.NetworkDao
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultNetworkDao(
    database: TvManiacDatabase,
) : NetworkDao {

    private val networkQueries = database.networksQueries

    override fun upsert(networks: Networks) {
        networkQueries.transaction {
            networkQueries.upsert(
                id = networks.id,
                tmdb_id = networks.tmdb_id,
                name = networks.name,
                logo_path = networks.logo_path,
            )
        }
    }

    override fun upsert(networks: List<Networks>) {
        networks.forEach { upsert(it) }
    }
}
