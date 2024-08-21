package com.suryadigital.leo.basedb

import com.suryadigital.leo.inlineLogger.getInlineLogger
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.io.Closeable
import java.sql.Connection
import java.util.Properties

private val logger = getInlineLogger(Database::class)

/**
 * Provides functions to interact with the Database.
 *
 * As soon as this class is instantiated, it creates a [HikariDataSource] for the given [Engine] which can be used to establish connection to the database.
 *
 * @param configuration metadata for the database connection.
 */
class Database(configuration: Configuration) : Closeable {
    private val dataSource: HikariDataSource

    init {
        logger.info { "Initializing database connection pool: $configuration" }
        val hikariConfig = HikariConfig()
        val properties = Properties()
        properties.setProperty("autosave", "conservative")
        hikariConfig.dataSourceProperties = properties
        when (configuration.engine) {
            Engine.MS_SQL_SERVER -> {
                hikariConfig.driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
                hikariConfig.jdbcUrl =
                    "jdbc:sqlserver://${configuration.host}:${configuration.port};databaseName=${configuration.name}"
            }
            Engine.POSTGRES -> {
                hikariConfig.jdbcUrl =
                    "jdbc:postgresql://${configuration.host}:${configuration.port}/${configuration.name}"
            }
            Engine.ORACLE -> {
                hikariConfig.jdbcUrl = "jdbc:oracle:thin:@//${configuration.host}:${configuration.port}/${configuration.name}"
            }
        }
        hikariConfig.username = configuration.userName
        hikariConfig.password = configuration.password
        hikariConfig.maximumPoolSize = configuration.maxConnections
        hikariConfig.connectionTimeout = configuration.connectionTimeoutMS
        dataSource = HikariDataSource(hikariConfig)
    }

    /**
     * Async method to establish a database connection and execute the given [block] using the [DSLContext] created using that connection.
     * This allows us to pass the queries that need to be executed to this function, and the function will take care of managing the connection.
     *
     * Note: This function commits the changes on a transaction level, i.e., if multiple queries are being executed in the given block, and one of them fails, the function will roll back all the queries that were executed.
     *
     * @param transactionIsolationLevel [TransactionIsolationLevel] for the given [block].
     * @param isReadOnly puts the connection in a read-only mode as a hint to the driver to enable database optimizations.
     * @param block query block that needs to be executed through the established database connection.
     *
     * @return a non-blocking cancellable future of type [R].
     *
     * @throws Exception when an exception occurs while executing the block.
     */
    suspend fun <R> queryAsync(
        transactionIsolationLevel: TransactionIsolationLevel = TransactionIsolationLevel.SERIALIZABLE,
        isReadOnly: Boolean = false,
        block: (DSLContext) -> R,
    ): Deferred<R> =
        coroutineScope {
            async(Dispatchers.IO) {
                dataSource.connection.use { conn ->
                    conn.autoCommit = false
                    conn.isReadOnly = isReadOnly
                    conn.transactionIsolation =
                        when (transactionIsolationLevel) {
                            TransactionIsolationLevel.SERIALIZABLE -> Connection.TRANSACTION_SERIALIZABLE
                            TransactionIsolationLevel.READ_COMMITTED -> Connection.TRANSACTION_READ_COMMITTED
                            TransactionIsolationLevel.REPEATABLE_READ -> Connection.TRANSACTION_REPEATABLE_READ
                        }
                    try {
                        val dslContext = DSL.using(conn, SQLDialect.POSTGRES)
                        block(dslContext)
                    } catch (e: Exception) {
                        logger.error(e) { "Rolling back transaction due to database error:" }
                        conn.rollback()
                        throw e
                    } finally {
                        conn.commit()
                        conn.autoCommit = true
                        conn.close()
                    }
                }
            }
        }

    /**
     * A wrapper function around [queryAsync] which awaits for the result to be returned.
     *
     * @param transactionIsolationLevel [TransactionIsolationLevel] for the given [block].
     * @param isReadOnly puts the connection in a read-only mode as a hint to the driver to enable database optimizations.
     * @param block query block that needs to be executed through the established database connection.
     *
     * @return value of type [R] defined by the [block].
     *
     * @throws Exception when an exception occurs while executing the block.
     */
    @Deprecated(
        message = "This method does not measure query metrics, and will be removed in the future. Please use timedQuery instead.",
        replaceWith =
            ReplaceWith(
                expression = "timedQuery(transactionIsolationLevel, isReadOnly, block)",
                imports = arrayOf("com.suryadigital.leo.basedb.timedQuery"),
            ),
        level = DeprecationLevel.WARNING,
    )
    suspend fun <R> query(
        transactionIsolationLevel: TransactionIsolationLevel = TransactionIsolationLevel.SERIALIZABLE,
        isReadOnly: Boolean = false,
        block: (DSLContext) -> R,
    ): R = queryAsync(transactionIsolationLevel, isReadOnly, block).await()

    /**
     * Closes the [dataSource] connection.
     */
    override fun close() {
        dataSource.close()
    }
}
