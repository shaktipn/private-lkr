package com.suryadigital.leo.distributedStore

import com.suryadigital.leo.testUtils.ResultGenerator

/**
 * Mock implementation used by test cases to simulate a [DistributedSynchronizedStore] implemenation.
 */
class MockDistributedSynchronizedStore : DistributedSynchronizedStore {
    private var getValueResultGenerator: ResultGenerator<String>? = null
    private var deleteResultGenerator: ResultGenerator<Any>? = null

    /**
     * Set the mock functionality for [DistributedSynchronizedStore.getValue].
     */
    internal fun setGetValueResultGenerator(resultGenerator: ResultGenerator<String>) {
        synchronized(this) {
            getValueResultGenerator = resultGenerator
        }
    }

    /**
     * Set the mock functionality for [DistributedSynchronizedStore.delete].
     */
    internal fun setDeleteResultGenerator(resultGenerator: ResultGenerator<Any>) {
        synchronized(this) {
            deleteResultGenerator = resultGenerator
        }
    }

    override suspend fun getValue(
        key: String,
        computeIfAbsent: suspend () -> DistributedSynchronizedStore.ComputeResult,
    ): String {
        return when (getValueResultGenerator) {
            is ResultGenerator.Response -> (getValueResultGenerator as ResultGenerator.Response<String>).value
            is ResultGenerator.Exception -> throw (getValueResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("GetValueResultGenerator has not been set.")
        }
    }

    override suspend fun delete(key: String) {
        when (deleteResultGenerator) {
            is ResultGenerator.Response -> (deleteResultGenerator as ResultGenerator.Response<Any>).value
            is ResultGenerator.Exception -> throw (deleteResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("DeleteResultGenerator has not been set.")
        }
    }
}
