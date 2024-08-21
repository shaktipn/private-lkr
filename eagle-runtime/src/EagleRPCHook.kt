package com.suryadigital.leo.eagle

import com.suryadigital.leo.rpc.LeoRPCError
import com.suryadigital.leo.rpc.LeoRPCException
import com.suryadigital.leo.rpc.LeoRPCRequest
import com.suryadigital.leo.rpc.LeoRPCResponse
import kotlin.jvm.Throws

/**
 * This abstract class is used to provide custom hooks in three cases:
 * - Before executing the RPC implementation.
 * - When an error occurs during the RPC call.
 * - After the RPC call is successfully made, and a valid response is returned.
 *
 * Currently, this is being used in Eagle generated code to provide developers a way to inject custom logic.
 */
@Suppress("Unused") // TODO: Remove once test cases are implemented: https://surya-digital.atlassian.net/browse/ST-532
interface EagleRPCHook<Req : LeoRPCRequest, Res : LeoRPCResponse, Err : LeoRPCError> {
    /**
     * Called as soon as the server implementation is initialized.
     * This is generally used to write the extra validations that should be added to the RPC call.
     * Validation exceptions then can be thrown as [LeoRPCException], with some of the commonly used ones being:
     * - [com.suryadigital.leo.rpc.LeoUnauthorizedException]: This exception is thrown when the user who makes the RPC call does not have correct permissions.
     * - [com.suryadigital.leo.rpc.LeoInvalidRequestException]: This exception is thrown when the request body does not meet the proper validations to proceed with the RPC call.
     * - RPC specific exceptions can also be thrown, and will be handled by the generated code.
     *
     * @param request request details of the RPC.
     *
     * @throws LeoRPCException
     */
    @Throws(LeoRPCException::class)
    suspend fun preRPCCall(request: Req)

    /**
     * Called when RPC call executes without any issues.
     *
     * @param request request details of the RPC.
     * @param response response of the RPC.
     */
    suspend fun onSuccess(
        response: Res,
        request: Req,
    )

    /**
     * Called if the server implementation throws error.
     *
     * @param request request details of the RPC.
     * @param error error that was thrown by the RPC implementation.
     */
    suspend fun onError(
        error: Err,
        request: Req,
    )
}
