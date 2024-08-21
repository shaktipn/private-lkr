package com.suryadigital.leo.testUtils

import com.suryadigital.leo.ktor.metrics.KtorMetrics
import com.suryadigital.leo.ktor.metrics.Metrics
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Helper function to run a block of code in a blocking manner, with [KtorMetrics] coroutine context.
 * This is generally used in test cases to call functions that require [KtorMetrics], and should not be called in production code.
 *
 * @param block function block which should be executed in the given [KtorMetrics] coroutine context, and return a value of type [T].
 */
@Suppress("ConvertLambdaToReference") // The suppressor is needed here because the lambda expression passed as an argument to the `runWithContext` function. `block: suspend () -> T` is being passed as a reference to the withContext function, so the warning is not necessary.
fun <T> runWithKtorMetricsContext(block: suspend () -> T): T {
    return runBlocking {
        withContext(KtorMetrics(Metrics())) {
            block()
        }
    }
}
