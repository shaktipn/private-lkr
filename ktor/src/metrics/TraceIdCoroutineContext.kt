package com.suryadigital.leo.ktor.metrics

import io.ktor.serialization.Configuration
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.call
import io.ktor.server.plugins.callid.callId
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Exposes a traceId that is obtained from Ktor's CallId plugin.
 *
 * To install: `install(TraceIdCoroutineContext)`.
 */
class TraceIdCoroutineContext {
    /**
     * Defines the [TraceId] plugin for the ktor server.
     */
    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, TraceIdCoroutineContext> {
        /**
         * Attribute key for [MetricsPlugin].
         */
        override val key: AttributeKey<TraceIdCoroutineContext> = AttributeKey("Leo TraceId Coroutine Context")

        private val phase: PipelinePhase = PipelinePhase("LeoTraceIdCoroutineContext")

        // Adding suppressor `ConvertLambdaToReference`- to execute `proceed()` command which is a function declared in
        // PipelineContext Interface. When converting to reference `proceed()` function is not properly imported by compiler.
        @Suppress("ConvertLambdaToReference")
        /**
         * Implementation required for the [TraceId] plugin to be installed before [ApplicationCallPipeline.Plugins] are instantiated.
         */
        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit,
        ): TraceIdCoroutineContext {
            val plugin = TraceIdCoroutineContext()
            pipeline.insertPhaseBefore(ApplicationCallPipeline.Plugins, phase)
            pipeline.intercept(phase) {
                val callId = call.callId
                if (callId != null) {
                    withContext(TraceId(callId)) {
                        proceed()
                    }
                } else {
                    proceed()
                }
            }
            return plugin
        }
    }
}

/**
 * Container class for storing traceId.
 *
 * @param traceId [String] for the given ktor [CoroutineContext].
 */
class TraceId(val traceId: String) : AbstractCoroutineContextElement(TraceId) {
    /**
     * Defines the unique identifier key for [TraceId] from which traceId value can be retrieved.
     */
    companion object Key : CoroutineContext.Key<TraceId>
}

/**
 * Get the [traceId] from [CoroutineContext] using [TraceId.Key].
 */
val CoroutineContext.traceId: String? get() = this[TraceId.Key]?.traceId
