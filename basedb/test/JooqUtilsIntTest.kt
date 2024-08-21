package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class JooqUtilsIntTest : JooqUtilsAbstractTest() {
    @Test
    fun testRecordGetIntOrNullWithIntReturnValue() {
        val insertSQL = """INSERT INTO "test"("integerColumn") VALUES (1); """
        val fetchSQL = """SELECT "integerColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getIntOrNull("integerColumn")
        assertEquals(1, resultValue)
    }

    @Test
    fun testRecordGetIntOrNullWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("integerColumn") VALUES (null); """
        val fetchSQL = """SELECT "integerColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getIntOrNull("integerColumn")
        assertEquals(null, resultValue)
    }

    @Test
    fun testRecordGetIntOrNullWithInvalidColumnName() {
        val insertSQL = """INSERT INTO "test"("integerColumn") VALUES (1); """
        val fetchSQL = """SELECT "integerColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getIntOrNull("invalidColumnName")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetIntOrNullWithInvalidFieldValue() {
        val insertSQL = """INSERT INTO "test"("stringColumn") VALUES ('1'); """
        val fetchSQL = """SELECT "stringColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getIntOrNull("stringColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetIntOrNullWithRangeMoreThanInt() {
        val insertSQL = """INSERT INTO "test"("decimalColumn") VALUES (2147483648); """
        val fetchSQL = """SELECT "decimalColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getIntOrNull("decimalColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetIntOrNullWithDecimalVaue() {
        val insertSQL = """INSERT INTO "test"("decimalColumn") VALUES (1.01); """
        val fetchSQL = """SELECT "decimalColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getIntOrNull("decimalColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetIntWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("integerColumn") VALUES (null); """
        val fetchSQL = """SELECT "integerColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getInt("integerColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }
}
