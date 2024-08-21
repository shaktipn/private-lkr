package com.suryadigital.leo.kedwig

/**
 * Defines a header that can be sent as a part of [Request].
 *
 * @property name key of the parameter.
 * @property value value of the parameter.
 */
data class QueryParameter(val name: String, val value: String) {
    /**
     * @return the string representation of [QueryParameter] with [name] and [value].
     */
    override fun toString(): String {
        return "$name=$value"
    }
}

/**
 * Defines the list of [QueryParameter] that can be sent as a part of [Request].
 *
 * @property params list of [QueryParameter] that can be iterated over.
 */
data class QueryParameters internal constructor(private val params: List<QueryParameter> = listOf()) : Iterable<QueryParameter> {
    /**
     * Get the first query parameter from the [QueryParameters] that matches the [name].
     *
     * @param name key of the [Header].
     *
     * @return [QueryParameter] if found or null.
     */
    fun getFirst(name: String): QueryParameter? {
        return params.find { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Get the value of the first query parameter from the [QueryParameters] that matches the [name].
     *
     * @param name key of the [QueryParameter].
     *
     * @return [String] value if found or null.
     */
    fun getFirstValue(name: String): String? {
        return getFirst(name)?.value
    }

    /**
     * Get a list of [QueryParameter] that match the [name].
     *
     * @param name key of the [QueryParameter].
     *
     * @return list of [QueryParameter].
     */
    operator fun get(name: String): List<QueryParameter> {
        return params.filter { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Get a list of values that match the [name].
     *
     * @param name key of the [QueryParameter].
     *
     * @return list of [String] values that match the given key.
     */
    fun getValues(name: String): List<String> {
        return get(name).map(QueryParameter::value)
    }

    /**
     * Get all the query parameters that are present in [QueryParameters].
     *
     * @return list of [QueryParameter].
     */
    fun getAll(): List<QueryParameter> {
        return params
    }

    /**
     * Get all the values that are present in [QueryParameters].
     *
     * @return list of [String] of values for all the [QueryParameters] present.
     */
    fun getAllValues(): List<String> {
        return params.map(QueryParameter::value)
    }

    /**
     * Get all the names (keys) that are present in [QueryParameters].
     *
     * @return list of [String] of names for all the [QueryParameters] present.
     */
    fun getAllNames(): List<String> {
        return params.map(QueryParameter::name)
    }

    /**
     * @return an [Iterator] for [params].
     */
    override fun iterator(): Iterator<QueryParameter> {
        return params.iterator()
    }
}

/**
 * Annotates the DSL for [QueryParameters].
 */
@DslMarker
annotation class QueryParametersDsl

/**
 * This is an implementation detail to enable the query parameters DSL - do not use directly.
 *
 * @param storage array list in which the [QueryParameter] should be stored.
 */
@QueryParametersDsl
class QueryParametersBuilder(storage: ArrayList<QueryParameter> = ArrayList()) :
    MutableList<QueryParameter> by storage {
    /**
     * Add a [QueryParameter] to the builder.
     *
     * @param name key of the [Header].
     * @param value value of the [Header].
     */
    fun queryParameter(
        name: String,
        value: String,
    ) {
        add(QueryParameter(name, value))
    }

    /**
     * Add query parameters to the builder by using the `to` infix. This function must be called on the name of the [QueryParameter].
     *
     * @param value value of the [QueryParameter].
     */
    infix fun String.to(value: String) {
        add(QueryParameter(this, value))
    }

    /**
     * Checks for the equality of [QueryParametersBuilder] based on its content.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other as QueryParametersBuilder
        if (size != other.size) {
            return false
        }
        return (0 until size).none { this[it] != other[it] }
    }

    /**
     * @return a hash code for [QueryParametersBuilder] object.
     */
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

/**
 * DSL function to create query parameters.
 *
 * @param block block of code that performs operations on [QueryParametersBuilder].
 *
 * @return the built [QueryParameters] that was defined using the operations done in the [block].
 */
fun queryParameters(block: QueryParametersBuilder.() -> Unit): QueryParameters {
    val qpb = QueryParametersBuilder().apply(block)
    return QueryParameters(qpb)
}
