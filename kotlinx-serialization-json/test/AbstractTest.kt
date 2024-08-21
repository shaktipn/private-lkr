package com.suryadigital.leo.kotlinxserializationjson

import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.asserter

internal abstract class AbstractTest {
    fun <T : Throwable> assertFailsWithCause(
        exceptionClass: KClass<T>,
        block: () -> Unit,
    ) {
        try {
            block()
            asserter.fail("Expected an exception to be thrown but was completed successfully.")
        } catch (e: Exception) {
            e.cause?.let {
                assertEquals<Any>(exceptionClass, it::class)
            } ?: asserter.fail("The exception did not have a cause.")
        }
    }
}
