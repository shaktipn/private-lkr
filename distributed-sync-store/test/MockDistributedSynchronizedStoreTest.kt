package com.suryadigital.leo.distributedStore

import com.suryadigital.leo.distributedStore.redis.exceptions.LockNotAcquiredException
import com.suryadigital.leo.distributedStore.redis.exceptions.ValueNotSetException
import com.suryadigital.leo.testUtils.ResultGenerator
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MockDistributedSynchronizedStoreTest {
    private var redisStore = MockDistributedSynchronizedStore()

    @Test
    fun testMockGetValueResponse() {
        runTest {
            redisStore.setGetValueResultGenerator(ResultGenerator.Response("Data"))
            val result =
                redisStore.getValue(key = "item") {
                    DistributedSynchronizedStore.ComputeResult("computedValue")
                }
            assertEquals("Data", result)
        }
    }

    @Test
    fun testMockGetValueException() {
        runTest {
            redisStore.setGetValueResultGenerator(ResultGenerator.Exception(ValueNotSetException("error message")))
            val exception =
                assertFailsWith<ValueNotSetException> {
                    redisStore.getValue(key = "item") {
                        DistributedSynchronizedStore.ComputeResult("computedValue")
                    }
                }
            assertEquals("error message", exception.message)
        }
    }

    @Test
    fun testMockGetValueResultGeneratorNotSet() {
        runTest {
            val exception =
                assertFailsWith<IllegalStateException> {
                    redisStore.getValue(key = "item") {
                        DistributedSynchronizedStore.ComputeResult("computedValue")
                    }
                }
            assertEquals("GetValueResultGenerator has not been set.", exception.message)
        }
    }

    @Test
    fun testMockDeleteResponse() {
        runTest {
            redisStore.setDeleteResultGenerator(ResultGenerator.Response("Data"))
            val result = redisStore.delete(key = "item")
            assertEquals(Unit, result)
        }
    }

    @Test
    fun testMockDeleteException() {
        runTest {
            redisStore.setDeleteResultGenerator(ResultGenerator.Exception(LockNotAcquiredException("error message")))
            val exception = assertFailsWith<LockNotAcquiredException> { redisStore.delete(key = "item") }
            assertEquals("error message", exception.message)
        }
    }

    @Test
    fun testMockDeleteResultGeneratorNotSet() {
        runTest {
            val exception =
                assertFailsWith<IllegalStateException> {
                    redisStore.delete(key = "item")
                }
            assertEquals("DeleteResultGenerator has not been set.", exception.message)
        }
    }
}
