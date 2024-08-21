package com.suryadigital.leo.basedb

/**
 * Defines the [isolation level](https://en.wikipedia.org/wiki/Isolation_(database_systems)#Isolation_levels) on which the database connection should be established.
 *
 * See [here](https://www.postgresql.org/docs/current/transaction-iso.html) for PostgreSQL specific information.
 */
enum class TransactionIsolationLevel {
    /**
     * Guarantees that any data read is committed at the moment it is read.
     */
    READ_COMMITTED,

    /**
     * Guarantees that any data read during the transaction remains consistent with the state of data before the transaction begins.
     */
    REPEATABLE_READ,

    /**
     * Higest form of isolation level that guarantees that all transactions are executed sequentially.
     */
    SERIALIZABLE,
}
