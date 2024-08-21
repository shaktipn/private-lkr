package com.suryadigital.leo.crypto

/**
 * HashVerifier enables you to save string securely by hashing them, and then verifying the string with a previously saved hash.
 */
interface HashVerifier {
    /**
     * Hashes a string that can be saved.
     *
     * @param value string to hash.
     *
     * @return hashed data.
     *
     * @throws HashingFailedException in the event of failing to hash [value].
     */
    @Throws(HashingFailedException::class)
    fun generateHash(value: String): ByteArray

    /**
     * Verifies a string against a previously saved hash.
     *
     * @param hashedValue previously saved hashed data.
     * @param actualValue string that needs to be validated against [hashedValue].
     *
     * @return [Boolean] true if [actualValue] matches [hashedValue], otherwise false.
     */
    fun isHashVerified(
        hashedValue: ByteArray,
        actualValue: String,
    ): Boolean
}
