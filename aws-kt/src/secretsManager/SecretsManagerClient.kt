package com.suryadigital.leo.awskt.secretsManager

/**
 * Interface defined for performing common AWS secrets management operations.
 */
interface SecretsManagerClient {
    /**
     * Get the value of a secret stored in AWS. All secrets have a corresponding name.
     * Each secret holds key-value pairs.
     *
     * @param secretName name of the secret in AWS.
     * @param key key of value to be obtained from the secret.
     *
     * @return value stored in the secret for the given key.
     *
     * @throws SecretNotFoundException if unable to find a secret with given [key].
     */
    @Throws(SecretNotFoundException::class)
    suspend fun getSecretValueByKey(
        secretName: String,
        key: String,
    ): String

    /**
     * Get secret by name. This will return a map of key-value pairs contained in the secret.
     *
     * @param secretName name of secret defined in AWS.
     *
     * @return [Map] of entries contained in the secret.
     */
    suspend fun getSecretByName(secretName: String): Map<String, String>
}
