package com.thomaskioko.tvmaniac.lists.nav.model

import kotlinx.serialization.Serializable

@Serializable
public data class ListDetailParam(
    val listId: Long,
    val name: String,
)
