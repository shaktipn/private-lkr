package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class JooqUtilsBooleanTest : JooqUtilsAbstractTest() {
    @Test
    fun testRecordGetBooleanOrNullWithBooleanReturnValue() {
        val insertSQL = """INSERT INTO "test"("booleanColumn") VALUES (true); """
        val fetchSQL = """SELECT "booleanColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getBooleanOrNull("booleanColumn")
        assertEquals(true, resultValue)
    }

    @Test
    fun testRecordGetBooleanOrNullWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("booleanColumn") VALUES (null); """
        val fetchSQL = """SELECT "booleanColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getBooleanOrNull("booleanColumn")
        assertEquals(null, resultValue)
    }

    @Test
    fun testRecordGetBooleanOrNullWithInvalidColumnName() {
        val insertSQL = """INSERT INTO "test"("booleanColumn") VALUES (true); """
        val fetchSQL = """SELECT "booleanColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getBooleanOrNull("invalidColumnName")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetBooleanOrNullWithInvalidFieldValue() {
        val insertSQL = """INSERT INTO "test"("stringColumn") VALUES ('1'); """
        val fetchSQL = """SELECT "stringColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getBooleanOrNull("stringColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetBooleanWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("booleanColumn") VALUES (null); """
        val fetchSQL = """SELECT "booleanColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getBoolean("booleanColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }
}
