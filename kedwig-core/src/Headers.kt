package com.suryadigital.leo.kedwig

/**
 * Defines a header that can be sent as a part of [Request] or received as a part of [Response].
 *
 * @property name key of the header.
 * @property value value of the header.
 */
data class Header(val name: String, val value: String) {
    /**
     * Checks for the equality of [Header] object.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Header

        if (!name.equals(other.name, ignoreCase = true)) return false
        if (value != other.value) return false

        return true
    }

    /**
     * @return a hash code for [Header] object.
     */
    override fun hashCode(): Int {
        var result = name.lowercase().hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    /**
     * @return the string representation for [Header] with its [name] and [value].
     */
    override fun toString(): String {
        return "$name:$value"
    }
}

/**
 * Defines the list of [Header] that can be sent as a part of [Request] or received as a part of [Response].
 *
 * @property headers list of [Header] that can be iterated over.
 */
data class Headers internal constructor(private val headers: List<Header> = listOf()) : Iterable<Header> {
    /**
     * Get the first header from the [Headers] that matches the [name].
     *
     * @param name key of the [Header].
     *
     * @return [Header] if found or null.
     */
    fun getFirst(name: String): Header? {
        return headers.find { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Get the value of the first header from the [Headers] that matches the [name].
     *
     * @param name key of the [Header].
     *
     * @return [String] value if found or null.
     */
    fun getFirstValue(name: String): String? {
        return getFirst(name)?.value
    }

    /**
     * Get a list of [Header] that match the [name].
     *
     * @param name key of the [Header].
     *
     * @return list of [Header].
     */
    operator fun get(name: String): List<Header> {
        return headers.filter { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Get a list of values that match the [name].
     *
     * @param name key of the [Header].
     *
     * @return list of [String] values that match the given key.
     */
    fun getValues(name: String): List<String> {
        return get(name).map(Header::value)
    }

    /**
     * Get all the headers that are present in [Headers].
     *
     * @return list of [Header].
     */
    fun getAll(): List<Header> {
        return headers
    }

    /**
     * Get all the values that are present in [Headers].
     *
     * @return list of [String] of values for all the [Headers] present.
     */
    fun getAllValues(): List<String> {
        return headers.map(Header::value)
    }

    /**
     * Get all the names (keys) that are present in [Headers].
     *
     * @return list of [String] of names for all the [Headers] present.
     */
    fun getAllNames(): List<String> {
        return headers.map(Header::name)
    }

    /**
     * @return an [Iterator] for [headers].
     */
    override fun iterator(): Iterator<Header> {
        return headers.iterator()
    }
}

/**
 * Annotates the DSL for [Headers].
 */
@DslMarker
annotation class HeadersDsl

/**
 * This is an implementation detail to enable the headers DSL - do not use directly.
 *
 * @param storage array list in which the [Header] should be stored.
 */
@HeadersDsl
class HeadersBuilder(storage: ArrayList<Header> = ArrayList()) : MutableList<Header> by storage {
    /**
     * Add a [Header] to the builder.
     *
     * @param name key of the [Header].
     * @param value value of the [Header].
     */
    fun header(
        name: String,
        value: String,
    ) {
        add(Header(name, value))
    }

    /**
     * Add headers to the builder by using the `to` infix. This function must be called on the name of the [Header].
     *
     * @param value value of the [Header].
     */
    infix fun String.to(value: String) {
        add(Header(this, value))
    }

    /**
     * Checks the equality of [HeadersBuilder] based on its content.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other as HeadersBuilder
        if (size != other.size) {
            return false
        }
        return (0 until size).none { this[it] != other[it] }
    }

    /**
     * @return a hash code for [HeadersBuilder] object.
     */
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

/**
 * DSL function to create [Headers].
 *
 * @param block block of code that performs operations on [HeadersBuilder].
 *
 * @return the built [Headers] that was defined using the operations done in the [block].
 */
fun headers(block: HeadersBuilder.() -> Unit): Headers {
    val hb = HeadersBuilder().apply(block)
    return Headers(hb)
}
