package com.thomaskioko.tvmaniac.traktlists.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.traktlists.api.TraktListDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktListRepository(
    private val traktListsStore: TraktListsStore,
    private val traktListDao: TraktListDao,
) : TraktListRepository {

    override fun observeLists(): Flow<List<TraktListEntity>> =
        traktListDao.observeAll().distinctUntilChanged()

    override suspend fun syncLists(forceRefresh: Boolean) {
        if (forceRefresh) {
            traktListsStore.fresh(key = STORE_KEY)
        } else {
            traktListsStore.get(key = STORE_KEY)
        }
    }

    private companion object {
        private const val STORE_KEY = "me"
    }
}
