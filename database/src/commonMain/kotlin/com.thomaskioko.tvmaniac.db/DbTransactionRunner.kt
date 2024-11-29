package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

interface DatabaseTransactionRunner {
  operator fun <T> invoke(block: () -> T): T
}

@Inject
@ContributesBinding(AppScope::class)
class DbTransactionRunner(private val db: TvManiacDatabase) : DatabaseTransactionRunner {
  override fun <T> invoke(block: () -> T): T {
    return db.transactionWithResult { block() }
  }
}
