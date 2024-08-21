package com.suryadigital.leo.basedb

import org.jooq.Condition
import org.jooq.SelectField
import org.jooq.SortField
import org.jooq.Table

/**
 * Data class that is used by [GenericPagination] to store all the query conditions for pagination.
 *
 * @property select list of [SelectField] which should be returned by the query after execution.
 * @property from name of the table on which the query should be executed.
 * @property where [Condition] to filter out the records before returning them.
 * @property order list of [SortField] which dicates how the records should be sorted.
 * @property offset filter to not return the first `n` records in the query result.
 * @property limit number of records that should be returned.
 */
data class QueryCondition(
    val select: List<SelectField<*>>,
    val from: Table<*>,
    val where: Condition,
    val order: List<SortField<*>>,
    val offset: Int,
    val limit: Int,
)
