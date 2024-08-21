package com.suryadigital.leo.rpc

/**
 * A provider for server to server authentication.
 *
 * Server to server requests are authenticated via a shared secret known only to the sender and receiver that
 * are communicating.
 *
 * An implementation of [ServerToServerAuthenticationProvider] is required to ensure that the secret is stored securely.
 */
interface ServerToServerAuthenticationProvider {
    /**
     * Retrieve secret to authenticate with.
     *
     * @return secret to authenticate with.
     */
    suspend fun getSecret(): String
}
