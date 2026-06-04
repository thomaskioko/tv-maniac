package com.thomaskioko.tvmaniac.db

import io.kotest.matchers.shouldBe
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.Test

class ForeignKeyCascadeMechanicTest {

    @Test
    fun `parent rebuild cascade-deletes children when foreign keys are on`() {
        connection().use { connection ->
            connection.seedParentAndChild()
            connection.exec("PRAGMA foreign_keys=ON")

            connection.rebuildParent()

            connection.childCount() shouldBe 0
        }
    }

    @Test
    fun `parent rebuild preserves children when foreign keys are off`() {
        connection().use { connection ->
            connection.seedParentAndChild()
            connection.exec("PRAGMA foreign_keys=OFF")

            connection.rebuildParent()

            connection.childCount() shouldBe 1
        }
    }

    private fun connection(): Connection = DriverManager.getConnection("jdbc:sqlite::memory:")

    private fun Connection.seedParentAndChild() {
        exec("CREATE TABLE parent (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
        exec(
            """
            CREATE TABLE child (
                id INTEGER NOT NULL PRIMARY KEY,
                parent_id INTEGER NOT NULL,
                FOREIGN KEY(parent_id) REFERENCES parent(id) ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        exec("INSERT INTO parent (id, name) VALUES (1, 'p')")
        exec("INSERT INTO child (id, parent_id) VALUES (10, 1)")
    }

    private fun Connection.rebuildParent() {
        exec("CREATE TABLE parent_new (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL, extra TEXT)")
        exec("INSERT INTO parent_new (id, name) SELECT id, name FROM parent")
        exec("DROP TABLE parent")
        exec("ALTER TABLE parent_new RENAME TO parent")
    }

    private fun Connection.childCount(): Int = createStatement().use { statement ->
        statement.executeQuery("SELECT COUNT(*) FROM child").use { resultSet ->
            resultSet.next()
            resultSet.getInt(1)
        }
    }

    private fun Connection.exec(sql: String) = createStatement().use { it.execute(sql) }
}
