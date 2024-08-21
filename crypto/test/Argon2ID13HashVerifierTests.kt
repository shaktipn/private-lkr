package com.suryadigital.leo.crypto

import kotlin.test.Test
import kotlin.test.assertEquals

class Argon2ID13HashVerifierTests {
    private val hashVerifier = Argon2ID13HashVerifier()

    @Test
    fun testPositiveHashVerification() {
        val string = "aZXc1e*6dty#fgt128)(&^nmkLdi2dty#fgt128)(&^nmkLdi23jw3#!3@&8ss4d2u34&jmd(fsl)snd"
        val hashedString = hashVerifier.generateHash(string)
        assertEquals(true, hashVerifier.isHashVerified(hashedString, string))
    }

    @Test
    fun testNegativeHashVerification() {
        val string = "aZXc1e*6dty#fgt128)(&^nmkLdi23jw3$$%^as%f23io21as5#!3@&8ss4d2u34&jmd(fsl)snd#!3@&8ss4d"
        val hashedString = hashVerifier.generateHash(string)
        assertEquals(false, hashVerifier.isHashVerified(hashedString, "invalidData"))
    }
}
