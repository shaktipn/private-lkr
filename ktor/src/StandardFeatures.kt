package com.suryadigital.leo.ktor

import com.suryadigital.leo.inlineLogger.getInlineLogger
import com.suryadigital.leo.ktor.metrics.MetricsCoroutineContext
import com.suryadigital.leo.ktor.metrics.MetricsPlugin
import com.suryadigital.leo.ktor.metrics.TraceIdCoroutineContext
import com.suryadigital.leo.ktor.metrics.metrics
import com.suryadigital.leo.rpc.LeoInvalidLLTException
import com.suryadigital.leo.rpc.LeoInvalidRequestException
import com.suryadigital.leo.rpc.LeoInvalidS2STokenException
import com.suryadigital.leo.rpc.LeoInvalidSLTException
import com.suryadigital.leo.rpc.LeoInvalidWTException
import com.suryadigital.leo.rpc.LeoServerException
import com.suryadigital.leo.rpc.LeoUnauthenticatedException
import com.suryadigital.leo.rpc.LeoUnauthorizedException
import com.suryadigital.leo.rpc.LeoUnsupportedClientException
import com.suryadigital.leo.rpc.LeoUserDisabledException
import com.typesafe.config.Config
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.deflate
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.compression.minimumSize
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.forwardedheaders.ForwardedHeaders
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.header
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.ktor.ext.inject
import org.slf4j.event.Level
import java.util.UUID

private val logger = getInlineLogger("leo-ktor")

@Serializable
private data class ServiceVersion(
    val version: String? = null,
    val git: String? = null,
) {
    fun toHeaderValue(): String {
        version?.let { v ->
            git?.let { g ->
                return "$v-$g"
            }
        }
        return "UNDEFINED"
    }
}

/**
 * [isServiceWarmedUp] is a boolean value which indicates if the service was warmed up or not.
 */
private var isServiceWarmedUp: Boolean = false

/**
 * Defines whether to enable or disable the warm-up service, a feature provided in [installStandardFeatures].
 */
sealed class ServiceWarmup {
    /**
     * Enable the warm-up service.
     *
     * @param warmUpService function block that performs the operations defined inside it, and returns a [Boolean] based on if the warm-up was performed successfully or not.
     */
    data class Enabled(
        val warmUpService: (() -> Boolean),
    ) : ServiceWarmup()

    /** Disable the warm-up service.
     */
    data object Disabled : ServiceWarmup()
}

/**
 * Installs the "standard" set of LeoRPC features.
 *
 * @param serviceWarmup indicates if the service needs to be warmed up or not. The reason we may need to warm up the service is:
 * - Usually the first few requests made to the server may take much time to respond because:
 *     - The server will be setting up the database,
 *     - Or APIClient connection pool,
 *     - Or loading data and persisting data on ram.
 * - So we usually send some stub requests to Database or APIClient which sets up the connection pool and increases the performance of the system.
 * - If we don't want to perform any warm-up activities, then we need to set the value of [serviceWarmup] to [ServiceWarmup.Disabled].
 * - If we want to perform any warm-up activities, then we need to set the value to [ServiceWarmup.Enabled] and to execute the task which warms up the service needs to be mentioned in [ServiceWarmup.Enabled.warmUpService].
 * - This [ServiceWarmup.Enabled.warmUpService] [function type](https://kotlinlang.org/docs/lambdas.html#function-types) returns a boolean value which indicates if the service was warmed up or not
 *
 * @param returnExceptionDetailsInResponse Indicates if we should be returning the detailed exception thrown by the server in the network response.
 * - This should be set to `true` only when standard features are required to be installed for server unit tests.
 * - It is set to `false` by default since it can cause a potential information leak to an outsider if the exception message contains any sensitive message and is returned as a part of the network response.
 * - For server unit test cases, we always execute the client, and we need access to the exact detailed exception thrown by the server for validation purposes. So we pass the exact exception thrown by the server to the client as a string in the response body.
 */
fun Application.installStandardFeatures(
    serviceWarmup: ServiceWarmup = ServiceWarmup.Disabled,
    returnExceptionDetailsInResponse: Boolean = false,
) {
    val jsonHandler: Json by inject()
    val versionValue =
        try {
            jsonHandler.decodeFromString(ServiceVersion.serializer(), this::class.java.getResource("/version.json")?.readText() ?: throw NullPointerException("`version.json` not found.")).toHeaderValue()
        } catch (e: Exception) {
            logger.warn(e) { "Unable to read the version." }
            "UNDEFINED"
        }

    val config: Config by inject()
    val serviceName =
        try {
            config.getString("serviceName")
        } catch (e: Exception) {
            logger.warn(e) { "Unable to read service name" }
            "UNDEFINED"
        }

    val instanceName = System.getenv("LEO_INSTANCE_NAME") ?: "UNDEFINED"

    val environment = System.getenv("LEO_ENVIRONMENT") ?: "UNDEFINED"

    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024)
        }
    }

    install(AutoHeadResponse)

    install(CallId) {
        retrieveFromHeader("X-Trace-Id")
        verify {
            try {
                UUID.fromString(it)
                true
            } catch (e: IllegalArgumentException) {
                logger.warn { "Got invalid X-Trace-Id header $it" }
                false
            }
        }
        generate {
            logger.debug { "Unable to read trace id from request header, generating a new one" }
            UUID.randomUUID().toString()
        }
        replyToHeader("X-Trace-Id")
    }

    install(CallLogging) {
        level = Level.INFO
        mdc("traceId", ApplicationCall::callId)
        mdc("serverRequestId") {
            UUID.randomUUID().toString()
        }
        mdc("serviceName") {
            serviceName
        }
        mdc("instanceName") {
            instanceName
        }
        mdc("serviceVersion") {
            versionValue
        }
        mdc("environment") {
            environment
        }
        mdc("clientIpAddress") { call ->
            call.request.header("X-Forwarded-For") ?: "UNDEFINED"
        }
        format { call ->
            val statusCode = call.response.status()?.value ?: 0
            val method = call.request.httpMethod.value
            val path = call.request.path()
            "Response Sent statusCode=$statusCode method=$method path=$path ${call.metrics}"
        }
    }

    install(MetricsPlugin)
    install(MetricsCoroutineContext)
    install(TraceIdCoroutineContext)

    install(DefaultHeaders) {
        header("X-Service-Version", versionValue)
    }

    install(ForwardedHeaders)
    install(XForwardedHeaders)

    install(StatusPages) {
        fun stringify(code: String): String {
            val payload =
                buildJsonObject {
                    put(
                        "meta",
                        buildJsonObject {
                            put("status", "ERROR")
                            put(
                                "error",
                                buildJsonObject {
                                    put("code", code)
                                },
                            )
                        },
                    )
                }
            return jsonHandler.encodeToString(JsonObject.serializer(), payload)
        }

        exception<LeoInvalidRequestException> { call, cause ->
            logger.warn(cause) { "Invalid request" }
            call.respond(HttpStatusCode.OK, stringify("INVALID_REQUEST"))
        }

        exception<LeoUnauthenticatedException> { call, cause ->
            logger.warn(cause) { "Unauthenticated request" }
            call.respond(HttpStatusCode.OK, stringify("UNAUTHENTICATED"))
        }
        exception<LeoUserDisabledException> { call, cause ->
            logger.warn(cause) { "User disabled" }
            call.respond(HttpStatusCode.OK, stringify("USER_DISABLED"))
        }
        exception<LeoInvalidSLTException> { call, cause ->
            logger.warn(cause) { "Invalid SLT" }
            call.respond(HttpStatusCode.OK, stringify("INVALID_SLT"))
        }
        exception<LeoInvalidLLTException> { call, cause ->
            logger.warn(cause) { "Invalid LLT" }
            call.respond(HttpStatusCode.OK, stringify("INVALID_LLT"))
        }
        exception<LeoInvalidWTException> { call, cause ->
            logger.warn(cause) { "Invalid WT" }
            call.respond(HttpStatusCode.OK, stringify("INVALID_WT"))
        }
        exception<LeoUnauthorizedException> { call, cause ->
            logger.warn(cause) { "Unauthorized request" }
            call.respond(HttpStatusCode.OK, stringify("UNAUTHORIZED"))
        }
        exception<LeoInvalidS2STokenException> { call, cause ->
            logger.warn(cause) { "Invalid server to server token" }
            call.respond(HttpStatusCode.OK, stringify("INVALID_S2S_TOKEN"))
        }
        exception<LeoUnsupportedClientException> { call, cause ->
            logger.warn(cause) { "Unsupported client request" }
            call.respond(HttpStatusCode.OK, stringify("UNSUPPORTED_CLIENT"))
        }

        exception<LeoServerException> { call, cause ->
            logger.error(cause) { "Server error" }
            cause.retryAfterSeconds?.let {
                call.response.header("Retry-After", it)
            }
            if (returnExceptionDetailsInResponse) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = stringify("$cause"),
                )
            } else {
                call.respond(message = HttpStatusCode.InternalServerError)
            }
        }
        exception<Exception> { call, cause ->
            logger.error(cause) { "Unexpected server error" }
            if (returnExceptionDetailsInResponse) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = stringify("$cause"),
                )
            } else {
                call.respond(message = HttpStatusCode.InternalServerError)
            }
        }
    }

    routing {
        get("healthCheck") {
            if (serviceWarmup is ServiceWarmup.Enabled) {
                if (isServiceWarmedUp) {
                    call.respond("Healthy")
                } else {
                    call.respond(HttpStatusCode.ServiceUnavailable)
                }
            } else {
                call.respond("Healthy")
            }
        }
    }
    if (serviceWarmup is ServiceWarmup.Enabled) {
        isServiceWarmedUp = serviceWarmup.warmUpService()
    }
}
