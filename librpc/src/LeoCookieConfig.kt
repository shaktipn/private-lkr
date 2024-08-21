package com.suryadigital.leo.rpc

import com.typesafe.config.Config
import io.github.config4k.extract
import java.lang.reflect.InvocationTargetException

/**
 * Stores the metadata used for setting a cookie in the Web Server.
 * @property name name of the cookie that is being added.
 * @property path the URL path that must exist in the requested URL in order to send the cookie.
 * @property maxAgeInSeconds the time in seconds after which the Cookie shall expire.
 * @property domain specifies which server can receive a cookie. Cookies are available on the specified server and its subdomains.
 * @property httpOnly if specified then the cookie can't be modified externally by javascript, can only be modified when it reaches the server.
 * @property secure if specified then the cookie is sent to the server with an encrypted request over the HTTPS protocol and cannot be sent with unsecured HTTP (except on localhost).
 * @property extensions map of extra properties for the cookie. (referesh time, same site, etc.)
 */
@Suppress("unused") // TODO: Remove once test cases are implemented: https://surya-digital.atlassian.net/browse/ST-537
data class LeoCookieConfig(
    val name: String,
    val path: String,
    val maxAgeInSeconds: Int,
    val domain: String,
    val httpOnly: Boolean,
    val secure: Boolean,
    val extensions: Map<String, String>,
) {
    companion object {
        /**
         * Parses a LeoCookieConfig object from a typesafe [Config].
         *
         * @throws [CookieConfigurationException] if [config] is not valid.
         */
        @Throws(CookieConfigurationException::class)
        fun fromConfig(config: Config): LeoCookieConfig {
            try {
                return config.extract()
            } catch (e: InvocationTargetException) {
                throw CookieConfigurationException(cause = e)
            }
        }
    }
}
