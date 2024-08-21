package com.suryadigital.leo.awskt.secretsManager

import com.suryadigital.leo.awskt.CONNECTION_ACQUISITION_TIMEOUT_SECONDS
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest

/**
 * Implementation for the [SecretsManagerClient].
 */
class SecretsManagerClientImpl(private val region: Region) : SecretsManagerClient {
    private val secretsManagerClient: SecretsManagerAsyncClient

    init {
        secretsManagerClient = getSecretsManagerAsyncClient()
    }

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
    override suspend fun getSecretValueByKey(
        secretName: String,
        key: String,
    ): String {
        val secrets = getSecretMap(secretName)
        return secrets[key] ?: throw SecretNotFoundException("Secret does not contain key: $key")
    }

    /**
     * Get secret by name. This will return a map of key-value pairs contained in the secret.
     *
     * @param secretName name of secret defined in AWS.
     *
     * @return [Map] of entries contained in the secret.
     */
    override suspend fun getSecretByName(secretName: String): Map<String, String> = getSecretMap(secretName)

    private suspend fun getSecretMap(secretName: String): Map<String, String> {
        val valueRequest =
            GetSecretValueRequest
                .builder()
                .secretId(secretName)
                .build()
        val valueResponse = secretsManagerClient.getSecretValue(valueRequest).await()
        return Json.parseToJsonElement(valueResponse.secretString()).jsonObject.map {
            it.key to it.value.jsonPrimitive.content
        }.toMap()
    }

    private fun getSecretsManagerAsyncClient(): SecretsManagerAsyncClient {
        val sdkAsyncHttpClient =
            NettyNioAsyncHttpClient
                .builder()
                .connectionAcquisitionTimeout(CONNECTION_ACQUISITION_TIMEOUT_SECONDS)
                .build()
        return SecretsManagerAsyncClient.builder()
            .httpClient(sdkAsyncHttpClient)
            .region(region)
            .build()
    }
}
