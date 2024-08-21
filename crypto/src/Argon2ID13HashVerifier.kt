package com.suryadigital.leo.crypto

import com.goterl.lazysodium.LazySodiumJava
import com.goterl.lazysodium.SodiumJava
import com.goterl.lazysodium.exceptions.SodiumException
import com.goterl.lazysodium.interfaces.PwHash
import com.goterl.lazysodium.interfaces.PwHash.ARGON2ID_MEMLIMIT_INTERACTIVE
import com.goterl.lazysodium.utils.LibraryLoader
import com.sun.jna.NativeLong
import com.suryadigital.leo.inlineLogger.getInlineLogger
import org.koin.core.component.KoinComponent
import java.util.Base64

private val logger = getInlineLogger(Argon2ID13HashVerifier::class)

/**
 * This is an implementation of [HashVerifier] that uses the Argon2ID13 Algorithm.
 *
 * It uses libsodium package for hashing string.
 * For more information, see [here](https://libsodium.gitbook.io/doc/password_hashing/default_phf)
 */
class Argon2ID13HashVerifier : HashVerifier, KoinComponent {
    /**
     * There are 4 modes of [LibraryLoader]:
     * - `PREFER_SYSTEM`: Try to load the system sodium first, if that fails — load the bundled version.
     * - `PREFER_BUNDLE`: Load the bundled native libraries first, then fallback to finding it in the system.
     * - `BUNDLED_ONLY`: Load the bundled version, ignoring the system.
     * - `SYSTEM_ONLY`: Load the system sodium only, ignoring the bundled.
     *
     * Using `BUNDLED_ONLY` configuration results in the library first extracting the `libsodium.so` from the jar file,
     * which results in some queries failing on the first run due to long extraction time.
     *
     * A better approach here is to `PREFER_SYSTEM`,
     * and make sure that `libsodium.so` is installed in your system (Docker or Local), for the optimal solution.
     * Even if `libsodium.so` is not found in the system, the library will fall back to the bundled version.
     */
    private val lazySodium = LazySodiumJava(SodiumJava(LibraryLoader.Mode.PREFER_SYSTEM))

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
    override fun generateHash(value: String): ByteArray {
        return try {
            val hashedString =
                lazySodium.cryptoPwHashStr(
                    value,
                    PwHash.OPSLIMIT_MODERATE,
                    NativeLong(
                        ARGON2ID_MEMLIMIT_INTERACTIVE.toLong(),
                    ),
                )
            Base64.getDecoder().decode(hashedString)
        } catch (e: SodiumException) {
            logger.debug(e) { "String Hashing failed." }
            throw HashingFailedException(e)
        }
    }

    /**
     * Verifies a string against a previously saved hash.
     *
     * @param hashedValue previously saved hashed data.
     * @param actualValue string that needs to be validated against [hashedValue].
     *
     * @return [Boolean] true if [actualValue] matches [hashedValue], otherwise false.
     */
    @Throws(HashingFailedException::class)
    override fun isHashVerified(
        hashedValue: ByteArray,
        actualValue: String,
    ): Boolean {
        return lazySodium.cryptoPwHashStrVerify(Base64.getEncoder().encodeToString(hashedValue), actualValue)
    }
}
