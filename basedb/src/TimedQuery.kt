package com.suryadigital.leo.basedb

import com.suryadigital.leo.ktor.metrics.metrics
import org.jooq.DSLContext
import kotlin.coroutines.coroutineContext

/**
 * A wrapper around [com.suryadigital.leo.basedb.Database.queryAsync] that records the time taken by the entire transaction to run in the current [com.suryadigital.leo.ktor.metrics.KtorMetrics] - [kotlin.coroutines.CoroutineContext].
 *
 * @param transactionIsolationLevel [TransactionIsolationLevel] for the given [block].
 * @param isReadOnly puts the connection in a read-only mode as a hint to the driver to enable database optimizations.
 * @param block query block that needs to be executed inside the generated database connection.
 *
 * @return value of type [R] defined by the [block].
 *
 * @throws Exception when an exception occurs while executing the block.
 */
@Suppress("ConvertLambdaToReference") // This is required because the metrics are not calculated properly if the function is passed as a reference.
suspend fun <R> Database.timedQuery(
    transactionIsolationLevel: TransactionIsolationLevel = TransactionIsolationLevel.REPEATABLE_READ,
    isReadOnly: Boolean = false,
    block: (DSLContext) -> R,
): R {
    return coroutineContext.metrics.timed(
        identifier = DBIDENTIFIER,
        block = {
            queryAsync(
                transactionIsolationLevel = transactionIsolationLevel,
                isReadOnly = isReadOnly,
                block = block,
            ).await()
        },
    )
}

private const val DBIDENTIFIER = "db"
