package com.thomaskioko.tvmaniac.db

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

public interface DatabaseTransactionRunner {
    public operator fun <T> invoke(block: () -> T): T
}

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DbTransactionRunner(private val db: TvManiacDatabase) : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T {
        return db.transactionWithResult { block() }
    }
}
