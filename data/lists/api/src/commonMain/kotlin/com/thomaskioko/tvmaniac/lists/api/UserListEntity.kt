package com.thomaskioko.tvmaniac.lists.api

public data class UserListEntity(
    val id: Long = 0,
    val traktId: Long? = null,
    val slug: String? = null,
    val name: String,
    val description: String?,
    val itemCount: Long,
    val createdAt: String,
    val posterPaths: List<String> = emptyList(),
)
