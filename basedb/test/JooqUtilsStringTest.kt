package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class JooqUtilsStringTest : JooqUtilsAbstractTest() {
    @Test
    fun testRecordGetStringOrNullWithStringReturnValue() {
        val insertSQL = """INSERT INTO "test"("stringColumn") VALUES ('test'); """
        val fetchSQL = """SELECT "stringColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery { it.execute(insertSQL) }
                database.timedQuery {
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getStringOrNull("stringColumn")
        assertEquals("test", resultValue)
    }

    @Test
    fun testRecordGetStringOrNullWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("stringColumn") VALUES (null); """
        val fetchSQL = """SELECT "stringColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getStringOrNull("stringColumn")
        assertEquals(null, resultValue)
    }

    @Test
    fun testRecordGetStringOrNullWithInvalidColumnName() {
        val insertSQL = """INSERT INTO "test"("stringColumn") VALUES ('test'); """
        val fetchSQL = """SELECT "stringColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getStringOrNull("invalidColumnName")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetStringOrNullWithInvalidFieldValueWithInt() {
        val insertSQL = """INSERT INTO "test"("integerColumn") VALUES (1); """
        val fetchSQL = """SELECT "integerColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getStringOrNull("integerColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetStringOrNullWithInvalidFieldValueWithBoolean() {
        val insertSQL = """INSERT INTO "test"("booleanColumn") VALUES (true); """
        val fetchSQL = """SELECT "booleanColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getStringOrNull("booleanColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetStringOrNullWithInvalidFieldValueWithFloat() {
        val insertSQL = """INSERT INTO "test"("floatColumn") VALUES (1.0); """
        val fetchSQL = """SELECT "floatColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getStringOrNull("floatColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetStringOrNullWithInvalidFieldValueWithDouble() {
        val insertSQL = """INSERT INTO "test"("doubleColumn") VALUES (1.0); """
        val fetchSQL = """SELECT "doubleColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getStringOrNull("doubleColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetStringOrNullWithInvalidFieldValueWithLong() {
        val insertSQL = """INSERT INTO "test"("longColumn") VALUES (1); """
        val fetchSQL = """SELECT "longColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getStringOrNull("longColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetStringWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("stringColumn") VALUES (null); """
        val fetchSQL = """SELECT "stringColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getString("stringColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }
}
