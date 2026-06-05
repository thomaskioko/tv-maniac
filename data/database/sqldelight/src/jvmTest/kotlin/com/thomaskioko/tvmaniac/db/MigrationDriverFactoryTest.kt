package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.thomaskioko.tvmaniac.core.logger.Logger
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MigrationDriverFactoryTest {

    private val logger = object : Logger {
        override fun error(message: String, throwable: Throwable) = Unit
        override fun error(tag: String, message: String) = Unit
    }

    private val schemaVersion = TvManiacDatabase.Schema.version

    @Test
    fun `should return the driver without wiping when the on-disk version matches`() {
        val driver = FakeSqlDriver(version = schemaVersion)
        val builder = FakeDatabaseDriverBuilder({ driver })

        val result = MigrationDriverFactory(builder, logger).create()

        result shouldBe driver
        builder.deletions shouldBe 0
        driver.closed shouldBe false
    }

    @Test
    fun `should close the stale driver then wipe and rebuild when the version does not match`() {
        val stale = FakeSqlDriver(version = schemaVersion - 1)
        val fresh = FakeSqlDriver(version = schemaVersion)
        val builder = FakeDatabaseDriverBuilder({ stale }, { fresh })

        val result = MigrationDriverFactory(builder, logger).create()

        result shouldBe fresh
        stale.closed shouldBe true
        builder.deletions shouldBe 1
    }

    @Test
    fun `should wipe and rebuild when opening the database throws`() {
        val broken = FakeSqlDriver(version = schemaVersion, throwOnQuery = true)
        val fresh = FakeSqlDriver(version = schemaVersion)
        val builder = FakeDatabaseDriverBuilder({ broken }, { fresh })

        val result = MigrationDriverFactory(builder, logger).create()

        result shouldBe fresh
        broken.closed shouldBe true
        builder.deletions shouldBe 1
    }

    @Test
    fun `should wipe and rebuild when building the driver throws`() {
        val fresh = FakeSqlDriver(version = schemaVersion)
        val builder = FakeDatabaseDriverBuilder({ throw IllegalStateException("cannot open") }, { fresh })

        val result = MigrationDriverFactory(builder, logger).create()

        result shouldBe fresh
        builder.deletions shouldBe 1
    }

    @Test
    fun `should read the user version from the driver`() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        driver.execute(null, "PRAGMA user_version = 36", 0)

        driver.userVersion() shouldBe 36L

        driver.close()
    }
}

private class FakeDatabaseDriverBuilder(
    private vararg val builds: () -> SqlDriver,
) : DatabaseDriverBuilder {

    private var index = 0

    var deletions: Int = 0
        private set

    override fun build(): SqlDriver = builds[index++].invoke()

    override fun deleteDatabase() {
        deletions++
    }
}

private class FakeSqlDriver(
    private val version: Long,
    private val throwOnQuery: Boolean = false,
) : SqlDriver {

    var closed: Boolean = false
        private set

    override fun <R> executeQuery(
        identifier: Int?,
        sql: String,
        mapper: (SqlCursor) -> QueryResult<R>,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?,
    ): QueryResult<R> {
        if (throwOnQuery) throw IllegalStateException("migration failed")
        return mapper(SingleLongCursor(version))
    }

    override fun execute(
        identifier: Int?,
        sql: String,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?,
    ): QueryResult<Long> = QueryResult.Value(0L)

    override fun newTransaction(): QueryResult<Transacter.Transaction> = error("unused")

    override fun currentTransaction(): Transacter.Transaction? = null

    override fun addListener(vararg queryKeys: String, listener: Query.Listener) = Unit

    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) = Unit

    override fun notifyListeners(vararg queryKeys: String) = Unit

    override fun close() {
        closed = true
    }
}

private class SingleLongCursor(private val value: Long) : SqlCursor {
    private var position = -1

    override fun next(): QueryResult<Boolean> {
        position++
        return QueryResult.Value(position == 0)
    }

    override fun getString(index: Int): String? = null

    override fun getLong(index: Int): Long = value

    override fun getBytes(index: Int): ByteArray? = null

    override fun getDouble(index: Int): Double? = null

    override fun getBoolean(index: Int): Boolean? = null
}
