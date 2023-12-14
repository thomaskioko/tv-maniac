package com.thomaskioko.tvmaniac.data.showdetails.api

import com.thomaskioko.tvmaniac.core.db.Networks
import com.thomaskioko.tvmaniac.core.db.Show_networks

interface NetworksDao {
    fun upsert(entity: Networks)
    fun upsert(showNetworks: Show_networks)
}
