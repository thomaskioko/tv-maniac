package com.thomaskioko.tvmaniac.core.store

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder

inline fun <Key : Any, Local : Any, Output : Any> storeBuilder(
  fetcher: Fetcher<Key, Local>,
  sourceOfTruth: SourceOfTruth<Key, Local, Output>,
): StoreBuilder<Key, Output> = StoreBuilder.from(fetcher, sourceOfTruth)

fun <Key : Any, Local : Any, Output : Any> SourceOfTruth<Key, Local, Output>.usingDispatchers(
  readDispatcher: CoroutineDispatcher,
  writeDispatcher: CoroutineDispatcher,
): SourceOfTruth<Key, Local, Output> {
  val wrapped = this
  return object : SourceOfTruth<Key, Local, Output> {
    override fun reader(key: Key): Flow<Output?> = wrapped.reader(key).flowOn(readDispatcher)

    override suspend fun write(key: Key, value: Local) = withContext(writeDispatcher) {
      wrapped.write(key, value)
    }

    override suspend fun delete(key: Key) = withContext(writeDispatcher) {
      wrapped.delete(key)
    }

    override suspend fun deleteAll() = withContext(writeDispatcher) {
      wrapped.deleteAll()
    }
  }
}
