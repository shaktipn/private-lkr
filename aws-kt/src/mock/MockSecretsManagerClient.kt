package com.suryadigital.leo.awskt.mock

import com.suryadigital.leo.awskt.secretsManager.SecretsManagerClient
import com.suryadigital.leo.testUtils.ResultGenerator
import java.lang.IllegalStateException

/**
 * Implementation for [SecretsManagerClient] to mock its functionality.
 */
@Suppress("Unused")
class MockSecretsManagerClient : SecretsManagerClient {
    private var getSecretValueByKeyResultGenerator: ResultGenerator<String>? = null
    private var getSecretByNameResultGenerator: ResultGenerator<Map<String, String>>? = null

    /**
     * Set the mock functionality for [SecretsManagerClient.getSecretValueByKey].
     */
    fun setGetSecretValueByKeyResultGenerator(resultGenerator: ResultGenerator<String>) {
        synchronized(this) {
            getSecretValueByKeyResultGenerator = resultGenerator
        }
    }

    /**
     * Set the mock functionality for [SecretsManagerClient.getSecretByName].
     */
    fun setGetSecretByNameResultGenerator(resultGenerator: ResultGenerator<Map<String, String>>) {
        synchronized(this) {
            getSecretByNameResultGenerator = resultGenerator
        }
    }

    override suspend fun getSecretValueByKey(
        secretName: String,
        key: String,
    ): String {
        return when (getSecretValueByKeyResultGenerator) {
            is ResultGenerator.Response -> (getSecretValueByKeyResultGenerator as ResultGenerator.Response<String>).value
            is ResultGenerator.Exception -> throw (getSecretValueByKeyResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("getSecretValueByKeyResultGenerator is not set on MockSecretsManagerClient")
        }
    }

    override suspend fun getSecretByName(secretName: String): Map<String, String> {
        return when (getSecretByNameResultGenerator) {
            is ResultGenerator.Response -> (getSecretByNameResultGenerator as ResultGenerator.Response<Map<String, String>>).value
            is ResultGenerator.Exception -> throw (getSecretByNameResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("getSecretByNameResultGenerator is not set on MockSecretsManagerClient")
        }
    }
}
