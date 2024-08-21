package com.suryadigital.leo.distributedStore

import com.suryadigital.leo.distributedStore.redis.RedisDistributedSynchronizedStore
import com.suryadigital.leo.distributedStore.redis.exceptions.LockNotAcquiredException
import com.suryadigital.leo.distributedStore.redis.exceptions.ValueNotSetException
import com.suryadigital.leo.inlineLogger.getInlineLogger
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisCommandExecutionException
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.AfterClass
import org.junit.BeforeClass
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import io.lettuce.core.RedisClient as LettuceRedisClient

private val logger = getInlineLogger(RedisDistributedSynchronizedStoreTest::class)

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisDistributedSynchronizedStoreTest {
    private val computeIfAbsentCallCount = AtomicInteger()

    companion object {
        private lateinit var connection: StatefulRedisConnection<String, String>
        private lateinit var redisClient: RedisCoroutinesCommands<String, String>
        private lateinit var redisContainer: GenericContainer<*>

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            redisContainer =
                GenericContainer(DockerImageName.parse("redis:5.0.6"))
                    .withExposedPorts(6379)
                    .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1))
            redisContainer.start()
            connection =
                LettuceRedisClient.create(
                    RedisURI.Builder.redis(redisContainer.host, redisContainer.firstMappedPort).build(),
                ).connect()
            redisClient = connection.coroutines()
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            runBlocking {
                connection.close()
                redisContainer.stop()
            }
        }
    }

    @BeforeTest
    fun setup() {
        runBlocking {
            startKoin {
                modules(
                    module {
                        single { redisClient }
                    },
                )
            }
            redisClient.flushall()
            computeIfAbsentCallCount.set(0)
        }
    }

    @AfterTest
    fun tearDown() {
        clearAllMocks()
        stopKoin()
    }

    @Test
    fun testPositiveWhenKeyExistsWithExpiresAtLifeTime() {
        val redisStore = getRedisStoreInstance()
        val result =
            runBlocking {
                redisClient.set(
                    TEST_KEY,
                    Json.encodeToString(
                        RedisDistributedSynchronizedStore.StoreValue(
                            value = "initialValue",
                            expiresAt = Instant.now().plusSeconds(10).toString(),
                        ),
                    ),
                )
                redisStore.getValue(TEST_KEY) {
                    DistributedSynchronizedStore.ComputeResult("computedValue")
                }
            }
        assertEquals("initialValue", result)
    }

    @Test
    fun testPositiveWhenKeyExistsWithInifiniteLifeTime() {
        val redisStore = getRedisStoreInstance()
        val result =
            runBlocking {
                redisClient.set(
                    TEST_KEY,
                    Json.encodeToString(
                        RedisDistributedSynchronizedStore.StoreValue(
                            value = "initialValue",
                            expiresAt = null,
                        ),
                    ),
                )
                redisStore.getValue(TEST_KEY) {
                    DistributedSynchronizedStore.ComputeResult("computedValue")
                }
            }
        assertEquals("initialValue", result)
    }

    @Test
    fun testPositiveWhenKeyExistsAndExpired() {
        val redisStore = getRedisStoreInstance()
        val result =
            runBlocking {
                redisClient.set(
                    TEST_KEY,
                    Json.encodeToString(
                        RedisDistributedSynchronizedStore.StoreValue(
                            value = "initialValue",
                            expiresAt = Instant.now().minusSeconds(10).toString(),
                        ),
                    ),
                )
                redisStore.getValue(TEST_KEY) {
                    DistributedSynchronizedStore.ComputeResult("computedValue")
                }
            }
        assertEquals("computedValue", result)
    }

    @Test
    fun testPositiveWhenKeyDoesNotExistWithInfiniteLifetime() {
        val redisStore = getRedisStoreInstance()
        val result =
            runBlocking {
                redisStore.getValue(TEST_KEY) {
                    DistributedSynchronizedStore.ComputeResult("computedValue")
                }
            }
        assertEquals("computedValue", result)
    }

    @Test
    fun testPositiveWhenKeyDoesNotExistWithExpiresAtLifetime() {
        val redisStore = getRedisStoreInstance()
        val result =
            runBlocking {
                redisStore.getValue(TEST_KEY) {
                    DistributedSynchronizedStore.ComputeResult(
                        "computedValue",
                        DistributedSynchronizedStore.Lifetime.ExpiresAt(
                            Instant.now().plusSeconds(1),
                        ),
                    )
                }
            }
        assertEquals("computedValue", result)
    }

    @Test
    fun testPositiveWhenKeyDoesNotExistWithTimeToLiveLifetime() {
        val redisStore = getRedisStoreInstance()
        val result =
            runBlocking {
                redisStore.getValue(TEST_KEY) {
                    DistributedSynchronizedStore.ComputeResult(
                        "computedValue",
                        DistributedSynchronizedStore.Lifetime.TimeToLive(5, TimeUnit.SECONDS),
                    )
                }
            }
        assertEquals("computedValue", result)
    }

    @Test
    fun testLockNotAcquiredException() {
        runTest {
            val redisStore = getRedisStoreInstance()
            val mockRedisClient = mockk<RedisCoroutinesCommands<String, String>>()
            coEvery { mockRedisClient.get(TEST_KEY) } returns null
            coEvery { mockRedisClient.get("${TEST_KEY}Lock") } returns null
            coEvery { mockRedisClient.set(any(), any(), any()) } returns null
            loadKoinModules(module { single { mockRedisClient } })
            val exception =
                assertFailsWith<LockNotAcquiredException> {
                    redisStore.getValue(TEST_KEY) {
                        DistributedSynchronizedStore.ComputeResult("computedValue")
                    }
                }
            assertEquals(exception.message, "Exhausted retries after 0 attempts.")
        }
    }

    @Test
    fun testLockNotAcquiredExceptionAfteMaxRetryCount() {
        runTest {
            val maxRetryCount = 2
            val redisStore = getRedisStoreInstance(maxRetryCount)
            val mockRedisClient = mockk<RedisCoroutinesCommands<String, String>>()
            coEvery { mockRedisClient.get(TEST_KEY) } returns null
            coEvery { mockRedisClient.get("${TEST_KEY}Lock") } returns null
            coEvery { mockRedisClient.set(any(), any(), any()) } returns null
            loadKoinModules(module { single { mockRedisClient } })
            val exception =
                assertFailsWith<LockNotAcquiredException> {
                    redisStore.getValue(TEST_KEY) {
                        DistributedSynchronizedStore.ComputeResult("computedValue")
                    }
                }
            assertEquals("Exhausted retries after $maxRetryCount attempts.", exception.message)
        }
    }

    @Test
    fun testValueNotSetExceptionForNullResponse() {
        runTest {
            val redisStore = getRedisStoreInstance()
            val mockRedisClient = mockk<RedisCoroutinesCommands<String, String>>()
            coEvery { mockRedisClient.incr("${TEST_KEY}Token") } returns 1L
            coEvery { mockRedisClient.get("${TEST_KEY}Token") } returns "1"
            coEvery { mockRedisClient.get(TEST_KEY) } returns null
            coEvery { mockRedisClient.get("${TEST_KEY}Lock") } returns null
            coEvery { mockRedisClient.set(any(), any(), any()) } returns "OK"
            coEvery { mockRedisClient.set(any(), any()) } returns null
            loadKoinModules(module { single { mockRedisClient } })
            assertFailsWith<ValueNotSetException> {
                redisStore.getValue(TEST_KEY) {
                    DistributedSynchronizedStore.ComputeResult("computedValue")
                }
            }
        }
    }

    @Test
    fun testValueNotSetExceptionForInvalidStatusResponse() {
        runTest {
            val redisStore = getRedisStoreInstance()
            val mockRedisClient = mockk<RedisCoroutinesCommands<String, String>>()
            coEvery { mockRedisClient.incr("${TEST_KEY}Token") } returns 1L
            coEvery { mockRedisClient.get("${TEST_KEY}Token") } returns "1"
            coEvery { mockRedisClient.get(TEST_KEY) } returns null
            coEvery { mockRedisClient.get("${TEST_KEY}Lock") } returns null
            coEvery { mockRedisClient.set(any(), any(), any()) } returns "OK"
            coEvery { mockRedisClient.set(any(), any()) } returns "INVALID_STATUS"
            loadKoinModules(module { single { mockRedisClient } })
            assertFailsWith<ValueNotSetException> {
                redisStore.getValue(TEST_KEY) {
                    DistributedSynchronizedStore.ComputeResult("computedValue")
                }
            }
        }
    }

    @Test
    fun testPositiveWhenKeyExistsWithMultipleReads() {
        val redisStore = getRedisStoreInstance()
        val initialValue =
            Json.encodeToString(
                RedisDistributedSynchronizedStore.StoreValue(
                    value = "initialValue",
                    expiresAt = Instant.now().plusSeconds(10).toString(),
                ),
            )
        runBlocking {
            redisClient.set(TEST_KEY, initialValue)
            List(10) { async { getValue(redisStore, "computedValue") } }.awaitAll()
            assertEquals(redisClient.get(TEST_KEY), initialValue)
        }
    }

    @Test
    fun testMultipleWritesThrowsLockNotAcquiredExceptionWithoutRetry() {
        val redisStore = getRedisStoreInstance()
        runTest {
            logger.debug { "Start testMultipleWritesThrowsLockNotAcquiredExceptionWithoutRetry" }
            redisClient.set(
                TEST_KEY,
                Json.encodeToString(
                    RedisDistributedSynchronizedStore.StoreValue(
                        value = "initialValue",
                        expiresAt = Instant.now().minusSeconds(10).toString(),
                    ),
                ),
            )
            supervisorScope {
                logger.debug { "Launching getValue coroutines" }
                List(10) { idx ->
                    async {
                        try {
                            getValue(redisStore, "computedValue$idx")
                        } catch (e: Exception) {
                            assertIs<LockNotAcquiredException>(e)
                            assertEquals("Exhausted retries after 0 attempts.", e.message)
                        }
                    }
                }.awaitAll()
            }
            delay(100)
            logger.debug { "Reading current value" }
            val curValue =
                Json.decodeFromString<RedisDistributedSynchronizedStore.StoreValue>(
                    redisClient.get(TEST_KEY) ?: throw IllegalStateException("Key $TEST_KEY does not exist"),
                )
            logger.debug { "curValue : $curValue" }
            assertNotEquals("initialValue", curValue.value)
            assertEquals(1, computeIfAbsentCallCount.toInt())
            logger.debug { "End testMultipleWritesThrowsLockNotAcquiredExceptionWithoutRetry" }
        }
    }

    @Test
    fun testMultipleWritesReturnsSameValueWhenKeyIsAbsentWithRetry() {
        val redisStore = getRedisStoreInstance(maxRetryCount = 5)
        runTest {
            logger.debug { "Start testMultipleWritesReturnsSameValueWhenKeyIsAbsentWithRetry" }
            val returnValues =
                List(10) { idx ->
                    async { getValue(redisStore, "computedValue$idx") }
                }.awaitAll()
            delay(100)
            assertTrue { returnValues.distinct().size == 1 }
            val curValue =
                Json.decodeFromString<RedisDistributedSynchronizedStore.StoreValue>(
                    redisClient.get(TEST_KEY) ?: throw IllegalStateException("Key $TEST_KEY does not exist"),
                )
            logger.debug { "curValue : $curValue" }
            assertNotEquals("initialValue", curValue.value)
            assertEquals(1, computeIfAbsentCallCount.toInt())
            logger.debug { "End testMultipleWritesReturnsSameValueWhenKeyIsAbsentWithRetry" }
        }
    }

    @Test
    fun testFirstWriteIsIgnoredIfLockTimeoutExceeded() {
        val redisStore = getRedisStoreInstance(maxRetryCount = 5)
        runBlocking {
            logger.debug { "Start testFirstWriteIsIgnoredIfLockTimeoutExceeded" }
            val firstWrite = async { getValue(redisStore, "computedValue0", delayMillis = 1000L) }
            val secondWrite = async { getValue(redisStore, "computedValue1") }
            val firstAttemptValue = firstWrite.await()
            delay(1)
            val secondAttemptValue = secondWrite.await()
            assertEquals("computedValue1", firstAttemptValue)
            assertEquals("computedValue1", secondAttemptValue)
            val curValue =
                Json.decodeFromString<RedisDistributedSynchronizedStore.StoreValue>(
                    redisClient.get(TEST_KEY) ?: throw IllegalStateException("Key $TEST_KEY does not exist"),
                )
            assertEquals("computedValue1", curValue.value)
            assertEquals(2, computeIfAbsentCallCount.get())
            logger.debug { "End testFirstWriteIsIgnoredIfLockTimeoutExceeded" }
        }
    }

    @Test
    fun testFenceTokenInvalidThrowsLockNotAcquiredException() {
        val redisStore = getRedisStoreInstance()
        val fenceTokenKey = "${TEST_KEY}Token"
        val exception =
            assertFailsWith<LockNotAcquiredException> {
                runBlocking {
                    redisStore.getValue(TEST_KEY) {
                        redisClient.incr(fenceTokenKey)
                        DistributedSynchronizedStore.ComputeResult(
                            "computedValue",
                            DistributedSynchronizedStore.Lifetime.Infinite,
                        )
                    }
                }
            }
        assertEquals("Fence token is no longer valid.", exception.cause?.message)
    }

    @Test
    fun testPostiveWhenFenceTokenOverflows() {
        val redisStore = getRedisStoreInstance()
        val fenceTokenKey = "${TEST_KEY}Token"
        runBlocking {
            redisClient.set(fenceTokenKey, "9223372036854775807")
            val curValue = getValue(redisStore, "computedValue")
            assertEquals("computedValue", curValue)
            assertEquals("0", redisClient.get(fenceTokenKey))
        }
    }

    @Test
    fun testPositiveForConsequentWritesAfterLockTimeout() {
        val redisStore = getRedisStoreInstance()
        runBlocking {
            logger.debug { "Start testPositiveForConsequentWritesAfterLockTimeout" }
            redisClient.set(
                TEST_KEY,
                Json.encodeToString(
                    RedisDistributedSynchronizedStore.StoreValue(
                        value = "initialValue",
                        expiresAt = Instant.now().minusSeconds(10).toString(),
                    ),
                ),
            )
            listOf(
                async {
                    getValue(
                        redisStore,
                        "computedValue0",
                        DistributedSynchronizedStore.Lifetime.TimeToLive(1, TimeUnit.MILLISECONDS),
                    )
                },
                async { delay(LOCK_TIMEOUT_MILLIS) },
            ).awaitAll()
            getValue(redisStore, "computedValue1")
            val curValue =
                Json.decodeFromString<RedisDistributedSynchronizedStore.StoreValue>(
                    redisClient.get(TEST_KEY) ?: throw IllegalStateException("Key $TEST_KEY does not exist"),
                )
            assertEquals("computedValue1", curValue.value)
            assertEquals(2, computeIfAbsentCallCount.get())
            logger.debug { "End testPositiveForConsequentWritesAfterLockTimeout" }
        }
    }

    @Test
    fun testIncrThrowsRedisCommandExecutionException() {
        runTest {
            val redisStore = getRedisStoreInstance()
            val mockRedisClient = mockk<RedisCoroutinesCommands<String, String>>()
            coEvery { mockRedisClient.incr("${TEST_KEY}Token") } throws RedisCommandExecutionException("INVALID_MESSAGE")
            coEvery { mockRedisClient.get(TEST_KEY) } returns null
            coEvery { mockRedisClient.get("${TEST_KEY}Lock") } returns null
            coEvery { mockRedisClient.set(any(), any(), any()) } returns "OK"
            coEvery { mockRedisClient.set(any(), any()) } returns "OK"
            loadKoinModules(module { single { mockRedisClient } })
            val exception =
                assertFailsWith<RedisCommandExecutionException> {
                    getValue(redisStore, "computedValue")
                }
            assertEquals(exception.message, "INVALID_MESSAGE")
        }
    }

    @Test
    fun testSetWithArgsReturnsInvalidStatusThrowsLockNotAcquiredException() {
        runTest {
            val redisStore = getRedisStoreInstance()
            val mockRedisClient = mockk<RedisCoroutinesCommands<String, String>>()
            coEvery { mockRedisClient.get("${TEST_KEY}Lock") } returns null
            coEvery { mockRedisClient.get(TEST_KEY) } returns null
            coEvery { mockRedisClient.set(any(), any(), any()) } returns "INVALID_STATUS"
            coEvery { mockRedisClient.set(any(), any()) } returns "OK"
            loadKoinModules(module { single { mockRedisClient } })

            val exception =
                assertFailsWith<LockNotAcquiredException> {
                    redisStore.getValue(TEST_KEY) {
                        DistributedSynchronizedStore.ComputeResult(
                            "computedValue",
                            DistributedSynchronizedStore.Lifetime.Infinite,
                        )
                    }
                }
            assertEquals(exception.message, "Exhausted retries after 0 attempts.")
        }
    }

    @Test
    fun testPositiveForDeleteKey() {
        val redisStore = getRedisStoreInstance()
        val result =
            runBlocking {
                redisClient.set(TEST_KEY, "initialValue")
                redisStore.delete(TEST_KEY)
                redisClient.get(TEST_KEY)
            }
        assertNull(result)
    }

    private suspend fun getValue(
        redisStore: RedisDistributedSynchronizedStore,
        computeResult: String,
        lifetime: DistributedSynchronizedStore.Lifetime = DistributedSynchronizedStore.Lifetime.Infinite,
        delayMillis: Long = 0L,
    ): String {
        return redisStore.getValue(TEST_KEY) {
            computeIfAbsentCallCount.incrementAndGet()
            if (delayMillis > 0L) {
                delay(delayMillis)
            }
            DistributedSynchronizedStore.ComputeResult(computeResult, lifetime)
        }
    }

    private fun getRedisStoreInstance(maxRetryCount: Int = 0): RedisDistributedSynchronizedStore =
        RedisDistributedSynchronizedStore(
            lockTimeoutMillis = LOCK_TIMEOUT_MILLIS,
            maxRetryCount = maxRetryCount,
        )
}

private const val TEST_KEY = "testKey"
private const val LOCK_TIMEOUT_MILLIS = 500L
