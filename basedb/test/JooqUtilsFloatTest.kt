package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JooqUtilsFloatTest : JooqUtilsAbstractTest() {
    @Test
    fun testRecordGetFloatOrNullWithFloatReturnValue() {
        val insertSQL = """INSERT INTO "test"("floatColumn") VALUES (1.0); """
        val fetchSQL = """SELECT "floatColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getFloatOrNull("floatColumn")
        assertTrue(compareFloatValuesForEquality("1.0".toFloat(), resultValue!!))
    }

    @Test
    fun testRecordGetFloatOrNullWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("floatColumn") VALUES (null); """
        val fetchSQL = """SELECT "floatColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getFloatOrNull("floatColumn")
        assertEquals(null, resultValue)
    }

    @Test
    fun testRecordGetFloatOrNullWithInvalidColumnName() {
        val insertSQL = """INSERT INTO "test"("floatColumn") VALUES (1.0); """
        val fetchSQL = """SELECT "floatColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getFloatOrNull("invalidColumnName")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetFloatOrNullWithInvalidFieldValue() {
        val insertSQL = """INSERT INTO "test"("stringColumn") VALUES ('1'); """
        val fetchSQL = """SELECT "stringColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getFloatOrNull("stringColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetFloatOrNullWithPositiveInfinityValue() {
        val insertSQL = """INSERT INTO "test"("decimalColumn") VALUES (4.40282346638528860e+38); """
        val fetchSQL = """SELECT "decimalColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getFloatOrNull("decimalColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetFloatOrNullWithNegativeInfinityValue() {
        val insertSQL = """INSERT INTO "test"("decimalColumn") VALUES (-2.40129846432481707e45); """
        val fetchSQL = """SELECT "decimalColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getFloatOrNull("decimalColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetFloatWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("floatColumn") VALUES (null); """
        val fetchSQL = """SELECT "floatColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getFloat("floatColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testCompareFloatValuesForEqualityPositive() {
        val float1 = 0.000001f
        val float2 = 0.0000001f
        assertTrue(compareFloatValuesForEquality(float1, float2))
    }

    @Test
    fun testCompareFloatValuesForEqualityNegative() {
        val float1 = 0.000001f
        val float2 = 0.000003f
        assertFalse(compareFloatValuesForEquality(float1, float2))
    }
}
