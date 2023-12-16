package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.core.db.Networks

interface NetworkDao {
    fun upsert(networks: Networks)
    fun upsert(networks: List<Networks>)
}
