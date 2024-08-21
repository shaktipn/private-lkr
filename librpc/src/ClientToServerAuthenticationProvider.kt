package com.suryadigital.leo.rpc

/**
 * A provider for client-to-server authentication.
 *
 * See documentation [here](https://coda.io/d/Training_dHOXTAtFBHO/Authentication_suauD#_luuaV)
 *
 * Note that it is the responsibility of the implementation to be thread-safe.
 */
sealed class ClientToServerAuthenticationProvider {
    /**
     * This class provides methods to retrieve, store, update SLT.
     */
    abstract class SLT : ClientToServerAuthenticationProvider() {
        /**
         * Retrieves current SLT.
         *
         * @return current SLT.
         */
        abstract suspend fun getSLT(): String

        /**
         * Store the updated SLT.
         *
         * @param value updated SLT.
         */
        abstract suspend fun setSLT(value: String)

        /**
         * Retrieves a fresh SLT by exchanging the LLT for a new SLT.
         *
         * This will be called when the current SLT has been deemed to be invalid.
         *
         * After this call is made, it is assumed that further calls to [getSLT] will also return the new value.
         *
         * @throws [LeoInvalidLLTException] implementations should throw [LeoInvalidLLTException] if the LLT that was available is no longer valid.
         */
        @Throws(LeoInvalidLLTException::class)
        abstract suspend fun refreshSLT()
    }

    /**
     * This class provides a method to retrieve WT.
     */
    abstract class WT : ClientToServerAuthenticationProvider() {
        /**
         * Retrieves current WT.
         *
         * @return current WT.
         */
        abstract suspend fun getWT(): String
    }
}
