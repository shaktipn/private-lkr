package com.suryadigital.leo.rpc

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ServerToServerAuthenticationTest {
    class ServerAuthenticationProvider(private val secret: String) : ServerToServerAuthenticationProvider {
        override suspend fun getSecret(): String {
            return secret
        }
    }

    class ServerAuthenticationValidator(private val secretProvider: ServerToServerAuthenticationProvider) : ServerToServerAuthenticationValidator {
        override suspend fun validateSecret(secret: String) {
            if (secret != secretProvider.getSecret()) {
                throw LeoUnauthenticatedException()
            }
        }
    }

    companion object {
        private val authenticationProvider: ServerToServerAuthenticationProvider = ServerAuthenticationProvider("MySecret")
        val authenticationValidator: ServerToServerAuthenticationValidator = ServerAuthenticationValidator(authenticationProvider)
    }

    @Test
    fun testServerAuthentication() {
        runBlocking {
            assertFailsWith<LeoUnauthenticatedException> { authenticationValidator.validateSecret("NotMySecret") }
        }
    }
}
