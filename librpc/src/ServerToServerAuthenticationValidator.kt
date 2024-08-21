package com.suryadigital.leo.rpc

/**
 * A validator for secrets provided by a [ServerToServerAuthenticationProvider].
 *
 * Server to server requests are authenticated via a shared secret known only to the sender and receiver that
 * are communicating.
 */
interface ServerToServerAuthenticationValidator {
    /**
     * Validate whether [secret] is correct.
     *
     * @param secret secret to validate.
     * @throws LeoUnauthenticatedException when [secret] is not valid.
     */
    suspend fun validateSecret(secret: String)
}
