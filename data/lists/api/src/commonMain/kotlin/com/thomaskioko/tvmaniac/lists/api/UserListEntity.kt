package com.thomaskioko.tvmaniac.lists.api

public data class UserListEntity(
    val id: Long,
    val slug: String,
    val name: String,
    val description: String?,
    val itemCount: Long,
    val createdAt: String,
    val posterPaths: List<String> = emptyList(),
)
