package com.suryadigital.leo.basedb

import com.typesafe.config.Config
import io.github.config4k.extract
import java.lang.reflect.InvocationTargetException

private const val DEFAULT_MAX_CONNECTIONS = 20
private const val DEFAULT_CONNECTION_TIMEOUT_MS = 5000L

/**
 * Configuration options for a database.
 *
 * @property engine [Engine] to use.
 * @property host host to connect to.
 * @property port port to connect to.
 * @property name database name to connect to.
 * @property userName username for connection.
 * @property password password for [userName].
 * @property maxConnections maximum number of connections to the database.
 * @property connectionTimeoutMS timeout for connections to the database.
 */
data class Configuration(
    val engine: Engine,
    val host: String,
    val port: Int,
    val name: String,
    val userName: String,
    val password: String,
    val maxConnections: Int = DEFAULT_MAX_CONNECTIONS,
    val connectionTimeoutMS: Long = DEFAULT_CONNECTION_TIMEOUT_MS,
) {
    companion object {
        /**
         * Parses a DBConfiguration object from a typesafe Config.
         *
         * @throws [ConfigurationException] if [config] is not valid.
         */
        @Throws(ConfigurationException::class)
        fun fromConfig(config: Config): Configuration {
            try {
                return config.extract()
            } catch (e: InvocationTargetException) {
                throw ConfigurationException(e)
            }
        }
    }

    /**
     * @return the string representation for [Configuration].
     */
    override fun toString(): String {
        return "Connection(engine=$engine, host=$host, port=$port, database=$name, user=$userName, password=****, maxConnections=$maxConnections, connectionTimeoutMS=$connectionTimeoutMS)"
    }
}

/**
 * Thrown when a [Configuration] is invalid.
 *
 * @param cause cause of exception.
 *
 * @constructor constructs exception.
 */
class ConfigurationException(cause: Throwable) : Exception(cause)
