package com.suryadigital.leo.rpc

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientToServerAuthenticationProviderTest {
    @Test
    fun testSLTAuthentication() {
        class SLTAuthenticationProvider(initialSLT: String) : ClientToServerAuthenticationProvider.SLT() {
            private var slt: String = initialSLT

            override suspend fun getSLT(): String {
                return slt
            }

            override suspend fun setSLT(value: String) {
                slt = value
            }

            override suspend fun refreshSLT() {
                slt = "refreshed"
            }
        }
        val sltAuthenticator = SLTAuthenticationProvider(initialSLT = "initial")
        runBlocking {
            assertEquals("initial", sltAuthenticator.getSLT())
            sltAuthenticator.setSLT("newSLT")
            assertEquals("newSLT", sltAuthenticator.getSLT())
            sltAuthenticator.refreshSLT()
            assertEquals("refreshed", sltAuthenticator.getSLT())
        }
    }

    @Test
    fun testWTAuthentication() {
        class WTAuthenticationProvider(initialWT: String) : ClientToServerAuthenticationProvider.WT() {
            private val wt: String = initialWT

            override suspend fun getWT(): String {
                return wt
            }
        }
        val wtAuthenticator = WTAuthenticationProvider(initialWT = "initial")
        runBlocking {
            assertEquals("initial", wtAuthenticator.getWT())
        }
    }
}
