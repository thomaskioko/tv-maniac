package com.thomaskioko.tvmaniac.db

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

interface DatabaseTransactionRunner {
    operator fun <T> invoke(block: () -> T): T
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DbTransactionRunner(private val db: TvManiacDatabase) : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T {
        return db.transactionWithResult { block() }
    }
}
