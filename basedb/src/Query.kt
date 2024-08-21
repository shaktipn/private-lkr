package com.suryadigital.leo.basedb

import com.suryadigital.leo.inlineLogger.getInlineLogger
import org.jooq.DSLContext

/**
 * Interface that defines the generic input given to a query.
 */
interface QueryInput

/**
 * Interface that defines the generic result fetched from the query.
 */
interface QueryResult

/**
 * Executes a query that takes in an input, and returns a single result.
 *
 * @param I input type.
 * @param R result type.
 */
abstract class SingleResultQuery<I : QueryInput, R : QueryResult> {
    /**
     * Call this function to execute the query.
     *
     * Note that this is the function callers should use, not [implementation].
     *
     * @param ctx [DSLContext] on which query will be executed.
     * @param input input to query
     *
     * @return result of query.
     */
    fun execute(
        ctx: DSLContext,
        input: I,
    ): R {
        val startTime = System.nanoTime()
        try {
            return implementation(ctx, input)
        } finally {
            val duration = System.nanoTime() - startTime
            getInlineLogger(javaClass::class).debug { "Query Execution Time - ${javaClass.canonicalName} - $duration ns" }
        }
    }

    /**
     * Subclasses must implement this method, and this is where the query implementation must be written.
     *
     * @param ctx [DSLContext] on which query will be executed.
     * @param input input to query
     *
     * @return result of query.
     */
    protected abstract fun implementation(
        ctx: DSLContext,
        input: I,
    ): R
}

/**
 * Executes a query that takes in an input, and returns an iterable result.
 *
 * @param I input type.
 * @param R iterable result type.
 */
abstract class IterableResultQuery<I : QueryInput, R : QueryResult> {
    /**
     * Call this function to execute the query.
     *
     * Note that this is the function callers should use, not [implementation].
     *
     * @param ctx [DSLContext] on which query will be executed.
     * @param input input to query.
     *
     * @return iterable result of the query.
     */
    fun execute(
        ctx: DSLContext,
        input: I,
    ): Iterable<R> {
        val startTime = System.nanoTime()
        try {
            return implementation(ctx, input)
        } finally {
            val duration = System.nanoTime() - startTime
            getInlineLogger(javaClass::class).debug { "Query Execution Time - ${javaClass.canonicalName} - $duration ns" }
        }
    }

    /**
     * Subclasses must implement this method, and this is where the query implementation must be written.
     *
     * @param ctx [DSLContext] on which query will be executed.
     * @param input input to query.
     *
     * @return iterable result of the query.
     */
    protected abstract fun implementation(
        ctx: DSLContext,
        input: I,
    ): Iterable<R>
}

/**
 * Executes a query that takes no input, and returns a single result.
 *
 * @param R result type.
 */
abstract class NoInputSingleResultQuery<R : QueryResult> {
    /**
     * Call this function to execute the query.
     *
     * Note that this is the function callers should use, not [implementation].
     *
     * @param ctx [DSLContext] on which query will be executed.
     *
     * @return result of query.
     */
    fun execute(ctx: DSLContext): R {
        val startTime = System.nanoTime()
        try {
            return implementation(ctx)
        } finally {
            val duration = System.nanoTime() - startTime
            getInlineLogger(javaClass::class).debug { "Query Execution Time - ${javaClass.canonicalName} - $duration ns" }
        }
    }

    /**
     * Subclasses must implement this method, and this is where the query implementation must be written.
     *
     * @param ctx [DSLContext] on which query will be executed.
     *
     * @return result of query.
     */
    protected abstract fun implementation(ctx: DSLContext): R
}

/**
 * Executes a query that takes in no input, and returns an iterable result.
 *
 * @param R iterable result type.
 */
abstract class NoInputIterableResultQuery<R : QueryResult> {
    /**
     * Call this function to execute the query.
     *
     * Note that this is the function callers should use, not [implementation].
     *
     * @param ctx [DSLContext] on which query will be executed.
     *
     * @return iterable result of the query.
     */
    fun execute(ctx: DSLContext): Iterable<R> {
        val startTime = System.nanoTime()
        try {
            return implementation(ctx)
        } finally {
            val duration = System.nanoTime() - startTime
            getInlineLogger(javaClass::class).debug { "Query Execution Time - ${javaClass.canonicalName} - $duration ns" }
        }
    }

    /**
     * Subclasses must implement this method, and this is where the query implementation must be written.
     *
     * @param ctx [DSLContext] on which query will be executed.
     * @return iterable result of the query.
     */
    protected abstract fun implementation(ctx: DSLContext): Iterable<R>
}

/**
 * Executes a query that takes in an input, and returns a single result or null.
 *
 * @param I input type.
 * @param R result type.
 */
abstract class SingleResultOrNullQuery<I : QueryInput, R : QueryResult?> {
    /**
     * Call this function to execute the query.
     *
     * Note that this is the function callers should use, not [implementation].
     *
     * @param ctx [DSLContext] on which query will be executed.
     * @param input input to query.
     *
     * @return result of query.
     */
    fun execute(
        ctx: DSLContext,
        input: I,
    ): R? {
        val startTime = System.nanoTime()
        try {
            return implementation(ctx, input)
        } finally {
            val duration = System.nanoTime() - startTime
            getInlineLogger(javaClass::class).debug { "Query Execution Time - ${javaClass.canonicalName} - $duration ns" }
        }
    }

    /**
     * Subclasses must implement this method, and this is where the query implementation must be written.
     *
     * @param ctx [DSLContext] on which query will be executed.
     * @param input input to query
     *
     * @return result of query or null.
     */
    protected abstract fun implementation(
        ctx: DSLContext,
        input: I,
    ): R?
}

/**
 * Executes a query that takes in an input, and returns no result.
 *
 * @param I input type.
 */
abstract class NoResultQuery<I : QueryInput> {
    /**
     * Call this function to execute the query.
     *
     * Note that this is the function callers should use, not [implementation].
     *
     * @param ctx [DSLContext] on which query will be executed.
     * @param input input to query.
     */
    fun execute(
        ctx: DSLContext,
        input: I,
    ) {
        val startTime = System.nanoTime()
        try {
            return implementation(ctx, input)
        } finally {
            val duration = System.nanoTime() - startTime
            getInlineLogger(javaClass::class).debug { "Query Execution Time - ${javaClass.canonicalName} - $duration ns" }
        }
    }

    /**
     * Subclasses must implement this method, and this is where the query implementation must be written.
     *
     * @param ctx [DSLContext] on which query will be executed.
     * @param input input to query.
     */
    protected abstract fun implementation(
        ctx: DSLContext,
        input: I,
    )
}

/**
 * Executes a query that takes in no input, and returns a single result or null.
 *
 * @param R result type.
 */
abstract class NoInputSingleResultOrNullQuery<R : QueryResult?> {
    /**
     * Call this function to execute the query.
     *
     * Note that this is the function callers should use, not [implementation].
     *
     * @param ctx [DSLContext] on which query will be executed.
     *
     * @return result of query.
     */
    fun execute(ctx: DSLContext): R? {
        val startTime = System.nanoTime()
        try {
            return implementation(ctx)
        } finally {
            val duration = System.nanoTime() - startTime
            getInlineLogger(javaClass::class).debug { "Query Execution Time - ${javaClass.canonicalName} - $duration ns" }
        }
    }

    /**
     * Subclasses must implement this method, and this is where the query implementation must be written.
     *
     * @param ctx [DSLContext] on which query will be executed.
     *
     * @return result of query or null.
     */
    protected abstract fun implementation(ctx: DSLContext): R?
}
