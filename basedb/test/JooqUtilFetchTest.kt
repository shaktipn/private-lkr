package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import org.jooq.exception.DataAccessException
import org.jooq.exception.TooManyRowsException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class JooqUtilFetchTest : JooqUtilsAbstractTest() {
    @Test
    fun testFetchExactlyOnePositive() {
        val insertSQL =
            """
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (1, true); 
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (2, false);
            """.trimIndent()
        val fetchSQL = """SELECT "integerColumn" FROM "test" WHERE "booleanColumn" = true; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery { it.execute(insertSQL) }
                database.timedQuery {
                    it.resultQuery(fetchSQL).fetchExactlyOne()
                }
            }
        val resultValue = result.getInt("integerColumn")
        assertEquals(1, resultValue)
    }

    @Test
    fun testFetchExactlyOneTooManyRowsException() {
        val insertSQL =
            """
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (1, true); 
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (2, true);
            """.trimIndent()
        val fetchSQL = """SELECT "integerColumn" FROM "test" WHERE "booleanColumn" = true; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.resultQuery(fetchSQL).fetchExactlyOne()
                    }
                }
            }
        val cause = exception.cause?.cause
        assertNotNull(cause)
        assertEquals(TooManyRowsException::class, cause::class)
    }

    @Test
    fun testFetchExactlyOneDBExceptionWithNoResult() {
        val insertSQL =
            """
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (1, false); 
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (2, false);
            """.trimIndent()
        val fetchSQL = """SELECT "integerColumn" FROM "test" WHERE "integerColumn" = 3; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.resultQuery(fetchSQL).fetchExactlyOne()
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testFetchExactlyOneDataAccessException() {
        val fetchSQL = """SELECT "integerColumn" FROM "test1" WHERE "booleanColumn" = true; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.resultQuery(fetchSQL).fetchExactlyOne()
                    }
                }
            }
        val cause = exception.cause?.cause
        assertNotNull(cause)
        assertEquals(DataAccessException::class, cause::class)
    }

    @Test
    fun testFetchOneOrNonePositive() {
        val insertSQL =
            """
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (1, true); 
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (2, false);
            """.trimIndent()
        val fetchSQL = """SELECT "integerColumn" FROM "test" WHERE "booleanColumn" = true; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.resultQuery(fetchSQL).fetchOneOrNone()
                }
            }
        val resultValue = result?.getInt("integerColumn")
        assertEquals(1, resultValue)
    }

    @Test
    fun testFetchOneOrNoneTooManyRowsException() {
        val insertSQL =
            """
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (1, true); 
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (2, true);
            """.trimIndent()
        val fetchSQL = """SELECT "integerColumn" FROM "test" WHERE "booleanColumn" = true; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.resultQuery(fetchSQL).fetchOneOrNone()
                    }
                }
            }
        val cause = exception.cause?.cause
        assertNotNull(cause)
        assertEquals(TooManyRowsException::class, cause::class)
    }

    @Test
    fun testFetchOneOrNoneWithNoResult() {
        val insertSQL =
            """
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (1, false); 
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (2, false);
            """.trimIndent()
        val fetchSQL = """SELECT "integerColumn" FROM "test" WHERE "integerColumn" = 3; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.resultQuery(fetchSQL).fetchOneOrNone()
                }
            }
        assertNull(result)
    }

    @Test
    fun testFetchOneOrNoneDataAccessException() {
        val fetchSQL = """SELECT "integerColumn" FROM "test1" WHERE "booleanColumn" = true; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.resultQuery(fetchSQL).fetchOneOrNone()
                    }
                }
            }
        val cause = exception.cause?.cause
        assertNotNull(cause)
        assertEquals(DataAccessException::class, cause::class)
    }
}
