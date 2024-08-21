package com.suryadigital.leo.basedb

import org.jooq.DSLContext

/**
 * Postgres implementation for [GenericPagination]
 */
class GenericPaginationPostgres : GenericPagination() {
    override fun implementation(
        ctx: DSLContext,
        input: Input,
    ): Result {
        return Result(
            record =
                ctx.select(
                    input.condition.select,
                )
                    .from(
                        input.condition.from,
                    )
                    .where(input.condition.where)
                    .orderBy(input.condition.order)
                    .offset(input.condition.offset)
                    .limit(input.condition.limit)
                    .fetch(),
        )
    }
}
