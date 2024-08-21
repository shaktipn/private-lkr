package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class JooqUtilsLongTest : JooqUtilsAbstractTest() {
    @Test
    fun testRecordGetLongOrNullWithLongReturnValue() {
        val insertSQL = """INSERT INTO "test"("longColumn") VALUES (2147483648); """
        val fetchSQL = """SELECT "longColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery { it.execute(insertSQL) }
                database.timedQuery {
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getLongOrNull("longColumn")
        assertEquals(2147483648, resultValue)
    }

    @Test
    fun testRecordGetLongOrNullWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("longColumn") VALUES (null); """
        val fetchSQL = """SELECT "longColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getLongOrNull("longColumn")
        assertEquals(null, resultValue)
    }

    @Test
    fun testRecordGetLongOrNullWithInvalidColumnName() {
        val insertSQL = """INSERT INTO "test"("longColumn") VALUES (2147483648); """
        val fetchSQL = """SELECT "longColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getLongOrNull("invalidColumnName")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetLongOrNullWithRangeMoreThanLong() {
        val insertSQL = """INSERT INTO "test"("decimalColumn") VALUES (9223372036854775809); """
        val fetchSQL = """SELECT "decimalColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getLongOrNull("decimalColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetLongOrNullWithDecimalVaue() {
        val insertSQL = """INSERT INTO "test"("decimalColumn") VALUES (1.01); """
        val fetchSQL = """SELECT "decimalColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getLongOrNull("decimalColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetLongOrNullWithInvalidFieldValue() {
        val insertSQL = """INSERT INTO "test"("stringColumn") VALUES ('1'); """
        val fetchSQL = """SELECT "stringColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getLongOrNull("stringColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetLongWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("longColumn") VALUES (null); """
        val fetchSQL = """SELECT "longColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getLong("longColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }
}
