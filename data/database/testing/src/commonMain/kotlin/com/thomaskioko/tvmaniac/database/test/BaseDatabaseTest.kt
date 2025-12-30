package com.thomaskioko.tvmaniac.database.test

import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.DatabaseFactory
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import kotlin.uuid.Uuid

internal expect fun createTestSqlDriver(name: String): SqlDriver

public abstract class BaseDatabaseTest {

    private val sqlDriver: SqlDriver by lazy {
        createTestSqlDriver("${this@BaseDatabaseTest::class.simpleName}_${Uuid.random()}")
    }

    protected val database: TvManiacDatabase by lazy { DatabaseFactory(sqlDriver).createDatabase() }

    public fun closeDb() {
        sqlDriver.close()
    }
}
