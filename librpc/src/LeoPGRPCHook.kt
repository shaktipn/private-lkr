package com.suryadigital.leo.rpc

import io.ktor.server.application.ApplicationCall
import java.util.UUID

/**
 * This abstract class is used to provide custom event hook in two cases:
 * - When an error occurs during the RPC call.
 * - After the RPC call is successfully made.
 *
 * Currently, this is being used in Pagination RPC to handle Audit log events.
 *
 * @param call ApplicationCall that is being used by an RPC request.
 * @param userId ID of the user who makes the request.
 *
 * @property onError Called if the server implementation throws error.
 * @property onSuccess Called when RPC call executes without an issues.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class LeoPGRPCHook(val call: ApplicationCall? = null, val userId: UUID? = null) {
    /**
     * @param code Error code which is thrown by the RPC when onError is called.
     */
    abstract suspend fun onError(code: String)

    /**
     * @param request Request parameter that is passed to the RPC during RPC call.
     */
    abstract suspend fun onSuccess(request: LeoRPCRequest)
}
