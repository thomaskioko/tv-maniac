package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import me.tatarka.inject.annotations.Inject

interface DatabaseTransactionRunner {
  operator fun <T> invoke(block: () -> T): T
}

@Inject
class DbTransactionRunner(private val db: TvManiacDatabase) : DatabaseTransactionRunner {
  override fun <T> invoke(block: () -> T): T {
    return db.transactionWithResult { block() }
  }
}
