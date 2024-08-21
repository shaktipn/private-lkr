package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import org.jooq.Condition
import org.jooq.Field
import org.jooq.Record
import org.jooq.SortField
import org.jooq.SortOrder
import org.jooq.Table
import org.jooq.impl.DSL.condition
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.table
import org.jooq.impl.DSL.trueCondition
import org.junit.Test
import kotlin.test.assertEquals

class GenericPaginationTest : QueryAbstractTest() {
    private fun getQueryCondition(
        selectCondition: List<Field<*>> =
            listOf(
                field(name("id")),
                field(name("name")),
            ),
        fromCondition: Table<Record> = table(name("test")),
        whereCondition: Condition = trueCondition(),
        orderByCondition: List<SortField<*>> = emptyList(),
        offset: Int = 0,
        limit: Int = 5,
    ): QueryCondition {
        return QueryCondition(
            select = selectCondition,
            from = fromCondition,
            where = whereCondition,
            order = orderByCondition,
            offset = offset,
            limit = limit,
        )
    }

    @Test
    fun testPositiveFor5Items() {
        runWithKtorMetricsContext {
            val queryCondition = getQueryCondition()
            val genericPagination = GenericPaginationPostgres()
            val result =
                database.timedQuery(isReadOnly = true) { ctx ->
                    genericPagination.execute(
                        ctx,
                        GenericPagination.Input(
                            condition = queryCondition,
                        ),
                    )
                }
            val record1 = result.record[0]
            val record2 = result.record[1]
            val record3 = result.record[2]
            val record4 = result.record[3]
            val record5 = result.record[4]
            assertEquals(5, result.record.size)
            assertEquals(1, record1.get("id"))
            assertEquals(2, record2.get("id"))
            assertEquals(3, record3.get("id"))
            assertEquals(4, record4.get("id"))
            assertEquals(5, record5.get("id"))
            assertEquals("TEST1", record1.get("name"))
            assertEquals("TEST2", record2.get("name"))
            assertEquals("TEST3", record3.get("name"))
            assertEquals("TEST4", record4.get("name"))
            assertEquals("TEST5", record5.get("name"))
        }
    }

    @Test
    fun testPositiveFor3Items() {
        runWithKtorMetricsContext {
            val queryCondition = getQueryCondition(limit = 3)
            val genericPagination = GenericPaginationPostgres()
            val result =
                database.timedQuery(isReadOnly = true) { ctx ->
                    genericPagination.execute(
                        ctx,
                        GenericPagination.Input(
                            condition = queryCondition,
                        ),
                    )
                }
            val record1 = result.record[0]
            val record2 = result.record[1]
            val record3 = result.record[2]
            assertEquals(3, result.record.size)
            assertEquals(1, record1.get("id"))
            assertEquals(2, record2.get("id"))
            assertEquals(3, record3.get("id"))
            assertEquals("TEST1", record1.get("name"))
            assertEquals("TEST2", record2.get("name"))
            assertEquals("TEST3", record3.get("name"))
        }
    }

    @Test
    fun testPositiveForPage2() {
        runWithKtorMetricsContext {
            val queryCondition = getQueryCondition(offset = 5)
            val genericPagination = GenericPaginationPostgres()
            val result =
                database.timedQuery(isReadOnly = true) { ctx ->
                    genericPagination.execute(
                        ctx,
                        GenericPagination.Input(
                            condition = queryCondition,
                        ),
                    )
                }
            val record1 = result.record[0]
            val record2 = result.record[1]
            assertEquals(2, result.record.size)
            assertEquals(6, record1.get("id"))
            assertEquals(7, record2.get("id"))
            assertEquals("TEST6", record1.get("name"))
            assertEquals("TEST7", record2.get("name"))
        }
    }

    @Test
    fun testPositiveForSorting() {
        runWithKtorMetricsContext {
            val queryCondition = getQueryCondition(orderByCondition = listOf(field(name("id")).sort(SortOrder.DESC)))
            val genericPagination = GenericPaginationPostgres()
            val result =
                database.timedQuery(isReadOnly = true) { ctx ->
                    genericPagination.execute(
                        ctx,
                        GenericPagination.Input(
                            condition = queryCondition,
                        ),
                    )
                }
            val record1 = result.record[0]
            val record2 = result.record[1]
            val record3 = result.record[2]
            val record4 = result.record[3]
            val record5 = result.record[4]
            assertEquals(5, result.record.size)
            assertEquals(7, record1.get("id"))
            assertEquals(6, record2.get("id"))
            assertEquals(5, record3.get("id"))
            assertEquals(4, record4.get("id"))
            assertEquals(3, record5.get("id"))
            assertEquals("TEST7", record1.get("name"))
            assertEquals("TEST6", record2.get("name"))
            assertEquals("TEST5", record3.get("name"))
            assertEquals("TEST4", record4.get("name"))
            assertEquals("TEST3", record5.get("name"))
        }
    }

    @Test
    fun testPositiveForWhereCondition() {
        runWithKtorMetricsContext {
            val queryCondition = getQueryCondition(whereCondition = condition("\"id\" > 2"))
            val genericPagination = GenericPaginationPostgres()
            val result =
                database.timedQuery(isReadOnly = true) { ctx ->
                    genericPagination.execute(
                        ctx,
                        GenericPagination.Input(
                            condition = queryCondition,
                        ),
                    )
                }
            val record1 = result.record[0]
            val record2 = result.record[1]
            val record3 = result.record[2]
            val record4 = result.record[3]
            val record5 = result.record[4]
            assertEquals(5, result.record.size)
            assertEquals(3, record1.get("id"))
            assertEquals(4, record2.get("id"))
            assertEquals(5, record3.get("id"))
            assertEquals(6, record4.get("id"))
            assertEquals(7, record5.get("id"))
            assertEquals("TEST3", record1.get("name"))
            assertEquals("TEST4", record2.get("name"))
            assertEquals("TEST5", record3.get("name"))
            assertEquals("TEST6", record4.get("name"))
            assertEquals("TEST7", record5.get("name"))
        }
    }
}
