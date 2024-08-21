package com.suryadigital.leo.ktUtils

import java.util.concurrent.TimeUnit
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Enables you to mark a property of your class as a delegated property that caches a produced value.
 *
 * This implementation is thread safe.
 *
 * This is a convenience wrapper around [Cached].
 *
 * Example usage:
 *
 * ```
 * private class SomeClass {
 *     val cachedProperty: Int by cached { doSomeComputation() }
 * }
 * ```
 *
 * ```
 * private class SomeClass {
 *     val cachedProperty: Int by cached(Cached.InvalidationPolicy.Duration(Duration.ofSeconds(1))) { doSomeComputation() }
 * }
 * ```
 *
 * NOTE: When using a DI framework, if you try to cache a value for a factory instance of a class, it won't cache the value, since with each new instance of the class, the values are computed again.
 * This function is generally useful for singleton objects. To make it useful in factory instances, all the properties that are delegated to [cached] should be stored in a companion object.
 *
 * @param V type of value being cached.
 * @param invalidationPolicy defines when to invalidate cached values.
 * @param producer function that produces a new value when called.
 *
 * @return instance of [Cached].
 */
fun <V> cached(
    invalidationPolicy: Cached.InvalidationPolicy = Cached.InvalidationPolicy.Never,
    producer: () -> V,
): Cached<V> = Cached(invalidationPolicy, producer)

/**
 * [Cached] is a Delegated Property (https://kotlinlang.org/docs/delegated-properties.html) that caches a produced value.
 *
 * This implementation is thread safe.
 *
 * @param V type of value being cached.
 * @property invalidationPolicy defines when to invalidate cached values.
 * @property producer function that produces a new value when called.
 */
class Cached<out V>(private val invalidationPolicy: InvalidationPolicy = InvalidationPolicy.Never, private val producer: () -> V) : ReadOnlyProperty<Any, V> {
    private var value: V? = null
    private var lastProducedAt = System.nanoTime()

    /**
     * @return the value for the object [thisRef].
     */
    override fun getValue(
        thisRef: Any,
        property: KProperty<*>,
    ): V {
        synchronized(this) {
            if (value == null) {
                return getNewValue()
            }
            val currentValue = value!!
            return when (invalidationPolicy) {
                is InvalidationPolicy.Never -> {
                    currentValue
                }
                is InvalidationPolicy.Duration -> {
                    if ((System.nanoTime() - lastProducedAt) > TimeUnit.NANOSECONDS.convert(invalidationPolicy.duration.seconds, TimeUnit.SECONDS)) {
                        getNewValue()
                    } else {
                        currentValue
                    }
                }
            }
        }
    }

    private fun getNewValue(): V {
        val newValue = producer()
        value = newValue
        lastProducedAt = System.nanoTime()
        return newValue
    }

    /**
     * Defines when to invalidate a previously cached value, and produce a new one.
     */
    sealed class InvalidationPolicy {
        /**
         * Indicates that once the value has been produced, it should never be produced again.
         */
        data object Never : InvalidationPolicy()

        /**
         * Indicates that if [duration] has passed since the last time the value was produced, a new value must be produced.
         *
         * @property duration
         */
        class Duration(val duration: java.time.Duration) : InvalidationPolicy()
    }
}
