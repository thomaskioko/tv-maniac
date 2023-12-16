package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.core.db.Networks
import com.thomaskioko.tvmaniac.core.db.Show_networks
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.data.showdetails.api.NetworksDao
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultNetworksDao(
    database: TvManiacDatabase,
) : NetworksDao {
    private val networksQueries = database.networksQueries
    private val networkShowsQueries = database.show_networksQueries

    override fun upsert(entity: Networks) {
        networksQueries.upsert(
            id = entity.id,
            tmdb_id = entity.tmdb_id,
            name = entity.name,
            logo_path = entity.logo_path,
        )
    }

    override fun upsert(showNetworks: Show_networks) {
        networkShowsQueries.upsert(
            show_id = showNetworks.show_id,
            network_id = showNetworks.network_id,
        )
    }
}
