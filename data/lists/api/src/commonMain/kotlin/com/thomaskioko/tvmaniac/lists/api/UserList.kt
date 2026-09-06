package com.thomaskioko.tvmaniac.lists.api

public data class UserList(
    val id: Long,
    val slug: String?,
    val name: String,
    val description: String?,
    val itemCount: Long,
    val isShowInList: Boolean,
)
