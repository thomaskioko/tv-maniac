package com.thomaskioko.tvmaniac.profile.api

import com.thomaskioko.tvmaniac.core.db.User
import kotlinx.coroutines.flow.Flow

interface ProfileDao {

    fun insert(traktUser: User)

    fun observeUserBySlug(slug: String): Flow<User?>

    fun observeUser(): Flow<User>

    fun getUser(): User?

    fun delete(slug: String)

    fun deleteAll()
}
