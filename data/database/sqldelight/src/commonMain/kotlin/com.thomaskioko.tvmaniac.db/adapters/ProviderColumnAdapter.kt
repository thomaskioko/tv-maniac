package com.thomaskioko.tvmaniac.db.adapters

import app.cash.sqldelight.ColumnAdapter
import com.thomaskioko.tvmaniac.db.Provider

public object ProviderColumnAdapter : ColumnAdapter<Provider, String> {
    override fun decode(databaseValue: String): Provider = Provider.valueOf(databaseValue)

    override fun encode(value: Provider): String = value.name
}
