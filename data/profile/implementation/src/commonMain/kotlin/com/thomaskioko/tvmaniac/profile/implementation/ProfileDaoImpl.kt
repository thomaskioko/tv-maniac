package com.thomaskioko.tvmaniac.profile.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.profile.api.ProfileDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ProfileDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : ProfileDao {

    override fun insert(traktUser: User) {
        database.userQueries.insertOrReplace(
            slug = traktUser.slug,
            full_name = traktUser.full_name,
            profile_picture = traktUser.profile_picture,
            user_name = traktUser.user_name,
            is_me = traktUser.is_me,
        )
    }

    override fun observeUserBySlug(slug: String): Flow<User?> {
        return database.userQueries.userBySlug(slug)
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
    }

    override fun observeUser(): Flow<User> {
        return database.userQueries.getCurrentUser()
            .asFlow()
            .mapToOne(dispatchers.io)
    }

    override fun getUser(): User? = database.userQueries.getCurrentUser().executeAsOneOrNull()

    override fun delete(slug: String) {
        database.userQueries.delete(slug)
    }

    override fun deleteAll() {
        database.userQueries.deleteAll()
    }
}
