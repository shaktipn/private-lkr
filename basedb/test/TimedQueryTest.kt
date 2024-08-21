package com.suryadigital.leo.basedb

import com.suryadigital.leo.ktor.metrics.KtorMetrics
import com.suryadigital.leo.ktor.metrics.Metrics
import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import kotlinx.coroutines.withContext
import org.jooq.exception.DataAccessException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TimedQueryTest : JooqUtilsAbstractTest() {
    companion object {
        private const val INSERT_STATEMENT = """INSERT INTO "test"("doubleColumn") VALUES (1.0); """
        private const val UPDATE_STATEMENT = """UPDATE "test" SET "doubleColumn"=2.0 WHERE "doubleColumn"=1.0; """
        private const val SELECT_STATEMENT = """SELECT "doubleColumn" FROM "test"; """
    }

    @Test
    fun testPositiveRepeatableReadIsolationLevel() {
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(INSERT_STATEMENT)
                    it.execute(UPDATE_STATEMENT)
                    it.fetchOne(SELECT_STATEMENT)
                }
            }
        val double = result?.getDoubleOrNull("doubleColumn")
        assertEquals(double, 2.0)
    }

    @Test
    fun testPositiveSerializableIsolationLevel() {
        val result =
            runWithKtorMetricsContext {
                database.timedQuery(TransactionIsolationLevel.SERIALIZABLE) {
                    it.execute(INSERT_STATEMENT)
                    it.execute(UPDATE_STATEMENT)
                    it.fetchOne(SELECT_STATEMENT)
                }
            }
        val double = result?.getDoubleOrNull("doubleColumn")
        assertEquals(double, 2.0)
    }

    @Test
    fun testPositiveReadCommittedIsolationLevel() {
        val result =
            runWithKtorMetricsContext {
                database.timedQuery(TransactionIsolationLevel.READ_COMMITTED) {
                    it.execute(INSERT_STATEMENT)
                    it.execute(UPDATE_STATEMENT)
                    it.fetchOne(SELECT_STATEMENT)
                }
            }
        val double = result?.getDoubleOrNull("doubleColumn")
        assertEquals(double, 2.0)
    }

    @Test
    fun testPostiveReadOnly() {
        val result =
            runWithKtorMetricsContext {
                withContext(KtorMetrics(Metrics())) {
                    database.timedQuery { it.execute(INSERT_STATEMENT) }
                    database.timedQuery(isReadOnly = true) { it.fetchOne(SELECT_STATEMENT) }
                }
            }
        val double = result?.getDoubleOrNull("doubleColumn")
        assertEquals(double, 1.0)
    }

    @Test
    fun testNegativeReadOnly() {
        runWithKtorMetricsContext {
            withContext(KtorMetrics(Metrics())) {
                database.timedQuery(isReadOnly = true) {
                    assertFailsWith<DataAccessException> { it.execute(INSERT_STATEMENT) }
                }
            }
        }
    }
}
