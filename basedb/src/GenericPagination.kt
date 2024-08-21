package com.suryadigital.leo.basedb

import org.jooq.Record

/**
 * This is used by the code generator in RPC-Gen to have a generic method of accessing paginated data from the database.
 *
 * Executes a [SingleResultQuery] that takes [QueryCondition] as input, and returns a [Result] of type [Record].
 */
abstract class GenericPagination :
    SingleResultQuery<GenericPagination.Input, GenericPagination.Result>() {
    /**
     * Defines the input taken by the query.
     */
    data class Input(
        /**
         * Class that defined the generic structure of the query parameters.
         */
        val condition: QueryCondition,
    ) : QueryInput

    /**
     * Defines the output record returned by the query.
     */
    data class Result(
        /**
         * Generic record of type [org.jooq.Result] from which the returned database records can be accessed.
         *
         * Note: This is a generic parameter used by generated code to then convert the result into a typesafe class, which is then returned to the RPC.
         * This is not the recommended way to return the [Result], and should not be used for manually written code.
         */
        val record: org.jooq.Result<Record>,
    ) : QueryResult
}
