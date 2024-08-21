package com.suryadigital.leo.testUtils

import com.suryadigital.leo.kedwig.APIClient
import com.suryadigital.leo.kedwig.Method
import com.suryadigital.leo.kotlinxserializationjson.getJsonObject
import com.suryadigital.leo.kotlinxserializationjson.getString
import com.suryadigital.leo.rpc.ClientToServerAuthenticationProvider
import com.suryadigital.leo.rpc.LeoRPCRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.test.assertEquals

/**
 * This function is used to check a specific exception that is being thrown in the server implementation.
 *
 * The parameters [authenticationProvider], [json] and [client] are the same paramaters that are required when we create an `RPCClientImpl` object.
 *
 * @param request RPC request of the test case.
 * @param expectedException exception along with the detailed message that is being tested. This exception object is the one the test case is written for.
 * @param routePath relative path that is present in the client implementation of that RPC. This is the URL path the RPC request is made to. For example: `agency/GetAccruedCommissionTransactions`.
 */
suspend fun executeRPCAndValidateInternalServerError(
    request: LeoRPCRequest,
    expectedException: Exception,
    routePath: String,
    authenticationProvider: ClientToServerAuthenticationProvider?,
    json: Json,
    client: APIClient,
) {
    val requestBody = json.encodeToString(serializer = JsonObject.serializer(), value = request.toJson())
    return sendRequestAsync(
        requestBody = requestBody,
        expectedException = expectedException,
        routePath = routePath,
        authenticationProvider = authenticationProvider,
        json = json,
        client = client,
    )
}

/**
 * Sends an asynchronous request to the RPC and asserts internal server error.
 */
private suspend fun sendRequestAsync(
    requestBody: String,
    expectedException: Exception,
    routePath: String,
    authenticationProvider: ClientToServerAuthenticationProvider?,
    json: Json,
    client: APIClient,
) {
    val response =
        when (authenticationProvider) {
            is ClientToServerAuthenticationProvider.WT -> {
                val wt = authenticationProvider.getWT()
                client.sendRequestAsync(
                    com.suryadigital.leo.kedwig.request {
                        path = routePath
                        method = Method.POST
                        cookies {
                            "Leo-RPC-WT" to wt
                        }
                        body(requestBody)
                    },
                )
            }

            is ClientToServerAuthenticationProvider.SLT -> {
                val slt = authenticationProvider.getSLT()
                client.sendRequestAsync(
                    com.suryadigital.leo.kedwig.request {
                        path = routePath
                        method = Method.POST
                        headers {
                            "Leo-RPC-SLT" to slt
                        }
                        body(requestBody)
                    },
                )
            }

            else -> {
                client.sendRequestAsync(
                    com.suryadigital.leo.kedwig.request {
                        path = routePath
                        method = Method.POST
                        body(requestBody)
                    },
                )
            }
        }
    val responseBody = json.parseToJsonElement(response.stringBody).jsonObject
    val meta = responseBody.getJsonObject("meta")
    val serverExceptionThrown = meta.getJsonObject("error").getString("code")
    assertEquals(serverExceptionThrown, "$expectedException")
    assertEquals(response.statusCode, INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE)
}

private const val INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE: Int = 500
