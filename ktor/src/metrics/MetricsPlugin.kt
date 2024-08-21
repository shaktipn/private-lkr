package com.suryadigital.leo.ktor.metrics

import io.ktor.serialization.Configuration
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.call
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase

/**
 * [MetricsPlugin] class is a custom plugin which can be used in Ktor applications.
 * It uses [Metrics] class for collecting a set of measurements for Ktor application.
 *
 * To use [MetricsPlugin] custom plugin in your application, you need to first install this plugin e.g.:
 *
 * ```
 *  fun Application.main() {
 *      install(MetricsFeature)
 *  }
 * ```
 *
 *  In your application, you can use [MetricsPlugin] like this:
 *
 * ```
 *  fun doSomeTask() {
 *      // perform some task
 *  }
 *
 *  class RecordMetrics(val call: ApplicationCall) {
 *      fun doSomeTask2() {
 *          call.metrics.timed("identifierName") {
 *              doSomeTask()
 *          }
 *      }
 *  }
 * ```
 */
class MetricsPlugin {
    /**
     * Defines the [MetricsPlugin] for the ktor server.
     */
    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, MetricsPlugin> {
        /**
         * Attribute key for [MetricsPlugin].
         */
        override val key: AttributeKey<MetricsPlugin> = AttributeKey("LeoMetricsFeature")

        /**
         * Defines the [PipelinePhase] for [MetricsPlugin].
         */
        val phase: PipelinePhase = PipelinePhase("LeoMetrics")
        internal val metricsKey = AttributeKey<Metrics>("LeoMetricsKey")

        /**
         * Implementation required for the [MetricsPlugin] to be installed before [ApplicationCallPipeline.Plugins] are instantiated.
         */
        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit,
        ): MetricsPlugin {
            val plugin = MetricsPlugin()
            pipeline.insertPhaseBefore(ApplicationCallPipeline.Plugins, phase)
            pipeline.intercept(phase) {
                val metrics = Metrics()
                call.attributes.put(metricsKey, metrics)
                proceed()
            }
            return plugin
        }
    }
}

/**
 * Get all the [Metrics] defined in the lifespan of an [ApplicationCall].
 */
val ApplicationCall.metrics: Metrics get() = attributes[MetricsPlugin.metricsKey]
