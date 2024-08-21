package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.table
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class QueryTest : QueryAbstractTest() {
    private val idField = field(name("id"))
    private val testId = 3

    companion object {
        data class Input(
            val minId: Int,
        ) : QueryInput

        data class Result(
            val id: Int,
        ) : QueryResult
    }

    @Test
    fun testIterableResultQuery() {
        class IterableResultQueryPostgres : IterableResultQuery<Input, Result>() {
            override fun implementation(
                ctx: DSLContext,
                input: Input,
            ): Iterable<Result> {
                return ctx
                    .select(idField)
                    .from("test")
                    .where(idField.ge(input.minId))
                    .fetch()
                    .map {
                        Result(
                            id = it.get("id") as Int,
                        )
                    }
            }
        }
        val iterableResultQuery = IterableResultQueryPostgres()
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    iterableResultQuery.execute(it, input = Input(minId = testId))
                }
            }.toList()
        assertEquals(5, result.size)
        assertEquals(listOf(3, 4, 5, 6, 7), result.map(Result::id))
    }

    @Test
    fun testNoInputIterableResultQuery() {
        class NoInputIterableResultQueryPostgres : NoInputIterableResultQuery<Result>() {
            override fun implementation(ctx: DSLContext): Iterable<Result> {
                return ctx
                    .select(idField)
                    .from("test")
                    .where(idField.ge(testId))
                    .fetch()
                    .map {
                        Result(
                            id = it.get("id") as Int,
                        )
                    }
            }
        }
        val iterableResultQuery = NoInputIterableResultQueryPostgres()
        val result =
            runWithKtorMetricsContext {
                database.timedQuery(block = iterableResultQuery::execute)
            }
        assertEquals(5, result.count())
        assertEquals(listOf(3, 4, 5, 6, 7), result.map(Result::id))
        assertFalse(result.any { it.id < testId })
    }

    @Test
    fun testNoInputSingleResultOrNullQueryWithResult() {
        class NoInputSingleResultOrNullQueryPostgres : NoInputSingleResultOrNullQuery<Result>() {
            override fun implementation(ctx: DSLContext): Result? {
                return ctx
                    .select(idField)
                    .from("test")
                    .where(idField.eq(testId))
                    .fetchOneOrNone()
                    ?.map {
                        Result(
                            id = it.get("id") as Int,
                        )
                    }
            }
        }
        val iterableResultQuery = NoInputSingleResultOrNullQueryPostgres()
        val result =
            runWithKtorMetricsContext {
                database.timedQuery(block = iterableResultQuery::execute)
            }
        assertEquals(testId, result?.id)
    }

    @Test
    fun testNoInputSingleResultOrNullQueryWithNull() {
        class NoInputSingleResultOrNullQueryPostgres : NoInputSingleResultOrNullQuery<Result>() {
            override fun implementation(ctx: DSLContext): Result? {
                return ctx
                    .select(idField)
                    .from("test")
                    .where(idField.eq(10))
                    .fetchOneOrNone()
                    ?.map {
                        Result(
                            id = it.get("id") as Int,
                        )
                    }
            }
        }
        val iterableResultQuery = NoInputSingleResultOrNullQueryPostgres()
        val result =
            runWithKtorMetricsContext {
                database.timedQuery(block = iterableResultQuery::execute)
            }
        assertNull(result?.id)
    }

    @Test
    fun testNoInputSingleResultQuery() {
        class NoInputSingleResultQueryPostgres : NoInputSingleResultQuery<Result>() {
            override fun implementation(ctx: DSLContext): Result {
                return ctx
                    .select(idField)
                    .from("test")
                    .where(idField.eq(testId))
                    .fetchExactlyOne()
                    .map {
                        Result(
                            id = it.get("id") as Int,
                        )
                    }
            }
        }
        val iterableResultQuery = NoInputSingleResultQueryPostgres()
        val result =
            runWithKtorMetricsContext {
                database.timedQuery(block = iterableResultQuery::execute)
            }
        assertEquals(testId, result.id)
    }

    @Test
    fun testSingleResultOrNullQueryWithResult() {
        class SingleResultOrNullQueryPostgres : SingleResultOrNullQuery<Input, Result>() {
            override fun implementation(
                ctx: DSLContext,
                input: Input,
            ): Result? {
                return ctx
                    .select(idField)
                    .from("test")
                    .where(idField.eq(input.minId))
                    .fetchOneOrNone()
                    ?.map {
                        Result(
                            id = it.get("id") as Int,
                        )
                    }
            }
        }
        val iterableResultQuery = SingleResultOrNullQueryPostgres()
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    iterableResultQuery.execute(
                        ctx = it,
                        input = Input(minId = testId),
                    )
                }
            }
        assertEquals(testId, result?.id)
    }

    @Test
    fun testSingleResultOrNullQueryWithNull() {
        class SingleResultOrNullQueryPostgres : SingleResultOrNullQuery<Input, Result>() {
            override fun implementation(
                ctx: DSLContext,
                input: Input,
            ): Result? {
                return ctx
                    .select(idField)
                    .from("test")
                    .where(idField.eq(input.minId))
                    .fetchOneOrNone()
                    ?.map {
                        Result(
                            id = it.get("id") as Int,
                        )
                    }
            }
        }
        val iterableResultQuery = SingleResultOrNullQueryPostgres()
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    iterableResultQuery.execute(
                        ctx = it,
                        input = Input(minId = 10),
                    )
                }
            }
        assertNull(result?.id)
    }

    @Test
    fun testSingleResultQuery() {
        class SingleResultQueryPostgres : SingleResultQuery<Input, Result>() {
            override fun implementation(
                ctx: DSLContext,
                input: Input,
            ): Result {
                return ctx
                    .select(idField)
                    .from("test")
                    .where(idField.eq(testId))
                    .fetchExactlyOne()
                    .map {
                        Result(
                            id = it.get("id") as Int,
                        )
                    }
            }
        }
        val iterableResultQuery = SingleResultQueryPostgres()
        val result =
            runWithKtorMetricsContext {
                database.timedQuery {
                    iterableResultQuery.execute(
                        ctx = it,
                        input = Input(minId = testId),
                    )
                }
            }
        assertEquals(testId, result.id)
    }

    @Test
    fun testNoResultQuery() {
        class NoResultQueryPostgres : NoResultQuery<Input>() {
            override fun implementation(
                ctx: DSLContext,
                input: Input,
            ) {
                val updated =
                    ctx
                        .update(table("test"))
                        .set(field(name("isActive")), true)
                        .where(idField.eq(input.minId))
                        .execute()
                assertEquals(1, updated)
            }
        }
        val iterableResultQuery = NoResultQueryPostgres()
        runWithKtorMetricsContext {
            database.timedQuery { ctx ->
                iterableResultQuery.execute(
                    ctx = ctx,
                    input = Input(minId = testId),
                )
                val isActive =
                    ctx
                        .select(field(name("isActive")))
                        .from("test")
                        .where(idField.eq(testId))
                        .fetchExactlyOne()
                        .get("isActive") as Boolean
                assertTrue(isActive)
            }
        }
    }
}
