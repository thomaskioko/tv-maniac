package com.thomaskioko.tvmaniac.lists.presenter.model

import kotlinx.collections.immutable.ImmutableList

public data class UserListItem(
    val id: Long,
    val name: String,
    val itemCount: Int,
    val itemCountLabel: String,
    val posterUrls: ImmutableList<String>,
)
