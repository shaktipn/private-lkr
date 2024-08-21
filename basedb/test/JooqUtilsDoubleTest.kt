package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JooqUtilsDoubleTest : JooqUtilsAbstractTest() {
    @Test
    fun testRecordGetDoubleOrNullWithDoubleReturnValue() {
        val insertSQL = """INSERT INTO "test"("doubleColumn") VALUES (1.0); """
        val fetchSQL = """SELECT "doubleColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getDoubleOrNull("doubleColumn")
        assertTrue(compareDoubleValuesForEquality(1.0, resultValue!!))
    }

    @Test
    fun testRecordGetDoubleOrNullWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("doubleColumn") VALUES (null); """
        val fetchSQL = """SELECT "doubleColumn" FROM "test"; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.fetchOne(fetchSQL)
                }
            }
        val resultValue = result?.getDoubleOrNull("doubleColumn")
        assertEquals(null, resultValue)
    }

    @Test
    fun testRecordGetDoubleOrNullWithInvalidColumnName() {
        val insertSQL = """INSERT INTO "test"("doubleColumn") VALUES (1.0); """
        val fetchSQL = """SELECT "doubleColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getDoubleOrNull("invalidColumnName")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetDoubleOrNullWithInvalidFieldValue() {
        val insertSQL = """INSERT INTO "test"("stringColumn") VALUES ('1.0'); """
        val fetchSQL = """SELECT "stringColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getDoubleOrNull("stringColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetDoubleOrNullWithPositiveInfinityValue() {
        val insertSQL = """INSERT INTO "test"("decimalColumn") VALUES ('2.79769313486231570e+308'); """
        val fetchSQL = """SELECT "decimalColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getDoubleOrNull("decimalColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetDoubleOrNullBetweenFloatAndPositiveInfinityValue() {
        val insertSQL = """INSERT INTO "test"("decimalColumn") VALUES (4.40282346638528860e+38); """
        val fetchSQL = """SELECT "decimalColumn" FROM "test"; """
        runWithKtorMetricsContext {
            database.timedQuery {
                it.execute(insertSQL)
                val resultValue = it.fetchOne(fetchSQL)?.getDoubleOrNull("decimalColumn")
                assertTrue(compareDoubleValuesForEquality(4.4028234663852884E38, resultValue!!))
            }
        }
    }

    @Test
    fun testRecordGetDoubleOrNullBetweenFloatAndNegativeInfinityValue() {
        val insertSQL = """INSERT INTO "test"("decimalColumn") VALUES (-2.40129846432481707e45); """
        val fetchSQL = """SELECT "decimalColumn" FROM "test"; """
        runWithKtorMetricsContext {
            database.timedQuery {
                it.execute(insertSQL)
                val resultValue = it.fetchOne(fetchSQL)?.getDoubleOrNull("decimalColumn")
                assertTrue(compareDoubleValuesForEquality(-2.401298464324817E45, resultValue!!))
            }
        }
    }

    @Test
    fun testRecordGetDoubleOrNullWithNegativeInfinityValue() {
        val insertSQL = """INSERT INTO "test"("decimalColumn") VALUES ('-5.94065645841246544e324'); """
        val fetchSQL = """SELECT "decimalColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getDoubleOrNull("decimalColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testRecordGetDoubleWithNullReturnValue() {
        val insertSQL = """INSERT INTO "test"("doubleColumn") VALUES (null); """
        val fetchSQL = """SELECT "doubleColumn" FROM "test"; """
        val exception =
            runWithKtorMetricsContext {
                assertFailsWith<DBException> {
                    database.timedQuery {
                        it.execute(insertSQL)
                        it.fetchOne(fetchSQL)?.getDouble("doubleColumn")
                    }
                }
            }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(DBException::class, cause::class)
    }

    @Test
    fun testCompareDoubleValuesForEqualityPositive() {
        val double1 = 0.000001
        val double2 = 0.0000008
        assertTrue(compareDoubleValuesForEquality(double1, double2))
    }

    @Test
    fun testCompareDoubleValuesForEqualityNegative() {
        val double1 = 0.000001
        val double2 = 0.000005
        assertFalse(compareDoubleValuesForEquality(double1, double2))
    }
}
