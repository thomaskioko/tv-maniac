package com.thomaskioko.tvmaniac.profile.presenter.model

import kotlinx.collections.immutable.ImmutableList

public data class ProfileListItem(
    val id: Long,
    val name: String,
    val itemCount: Int,
    val itemCountLabel: String,
    val posterUrls: ImmutableList<String>,
)
