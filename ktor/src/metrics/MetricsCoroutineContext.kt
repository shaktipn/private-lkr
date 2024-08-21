package com.suryadigital.leo.ktor.metrics

import io.ktor.server.application.Application
import io.ktor.server.application.Plugin
import io.ktor.server.application.call
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Exposes [Metrics] via a [KtorMetrics] coroutine context.
 *
 * To install: `install(MetricsCoroutineContext)`.
 *
 * Example usage:
 *
 * ```
 *  fun doSomeTask() {
 *      // perform some task
 *  }
 *
 *  class RecordMetrics(val call: ApplicationCall) {
 *      suspend fun doSomeTask2() {
 *          coroutineContext.metrics.timed("identifierName") {
 *              doSomeTask()
 *          }
 *      }
 *  }
 * ```
 */
class MetricsCoroutineContext {
    /**
     * Defines the [MetricsCoroutineContext] interceptor for [MetricsPlugin].
     */
    companion object Feature : Plugin<Application, Unit, MetricsCoroutineContext> {
        /**
         * Attribute key for [MetricsCoroutineContext].
         */
        override val key: AttributeKey<MetricsCoroutineContext> = AttributeKey("Leo Metrics Coroutine Context")

        private val phase: PipelinePhase = PipelinePhase("LeoMetricsCoroutineContext")

        // Adding suppressor `ConvertLambdaToReference`- to execute `proceed()` command which is a function declared in
        // PipelineContext Interface. When converting to reference `proceed()` function is not properly imported by compiler.
        @Suppress("ConvertLambdaToReference")
        /**
         * Implementation required for the [MetricsCoroutineContext] plugin interception when the ktor server starts.
         */
        override fun install(
            pipeline: Application,
            configure: Unit.() -> Unit,
        ): MetricsCoroutineContext {
            val feature = MetricsCoroutineContext()
            pipeline.insertPhaseAfter(MetricsPlugin.phase, phase)
            pipeline.intercept(phase) {
                withContext(KtorMetrics(call.metrics)) {
                    proceed()
                }
            }
            return feature
        }
    }
}

/**
 * Container class for storing ktor metrics.
 *
 * @param metrics [Metrics] object for the given ktor [CoroutineContext].
 */
class KtorMetrics(val metrics: Metrics) : AbstractCoroutineContextElement(KtorMetrics) {
    /**
     * Defines the unique identifier key for [KtorMetrics] from which metrics information can be retrieved.
     */
    companion object Key : CoroutineContext.Key<KtorMetrics>
}

/**
 * Get the [Metrics] from [CoroutineContext] using [KtorMetrics.Key].
 */
val CoroutineContext.metrics: Metrics get() = this[KtorMetrics.Key]?.metrics ?: throw IllegalStateException("Metrics aren't found in coroutine context")
