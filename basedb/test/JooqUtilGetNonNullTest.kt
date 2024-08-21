package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.name
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class JooqUtilGetNonNullTest : JooqUtilsAbstractTest() {
    @Test
    fun testGetNonNullValue() {
        val insertSQL =
            """
            INSERT INTO "test"("integerColumn", "booleanColumn") VALUES (1, null);
            """.trimIndent()
        val fetchSQL = """SELECT "integerColumn", "booleanColumn" FROM "test" WHERE "integerColumn" = 1; """
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                    it.fetchOne(fetchSQL)
                }
            }
        assertNotNull(result?.getNonNullValue(field = field(name("integerColumn"))))
        val nullException = assertFailsWith<DBException> { result?.getNonNullValue(field = field(name("booleanColumn"))) }
        assertEquals("Column value is null: booleanColumn", nullException.message)
        val exception = assertFailsWith<DBException> { result?.getNonNullValue(field = field(name("random"))) }
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(IllegalArgumentException::class, cause::class)
        assertEquals("java.lang.IllegalArgumentException: Field (\"random\") is not contained in Row (\"test\".\"integerColumn\", \"test\".\"booleanColumn\")", exception.message)
    }
}
