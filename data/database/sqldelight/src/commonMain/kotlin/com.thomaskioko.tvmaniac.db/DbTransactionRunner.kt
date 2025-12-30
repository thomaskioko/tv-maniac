package com.thomaskioko.tvmaniac.db

import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

public interface DatabaseTransactionRunner {
    public operator fun <T> invoke(block: () -> T): T
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DbTransactionRunner(private val db: TvManiacDatabase) : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T {
        return db.transactionWithResult { block() }
    }
}
