package com.suryadigital.leo.basedb

import com.typesafe.config.ConfigFactory.parseString
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfigurationTest {
    @Test
    fun testFromConfigAllProperties() {
        val config =
            parseString(
                """
                engine=POSTGRES
                host=foobar
                port=1234
                name=baz
                userName=foo
                password=bar
                maxConnections=5000
                connectionTimeoutMS=2000
                """.trimIndent(),
            )
        val actual = Configuration.fromConfig(config)
        val expected = Configuration(Engine.POSTGRES, "foobar", 1234, "baz", "foo", "bar", 5000, 2000)
        assertEquals(expected, actual)
    }

    @Test
    fun testFromConfigRequiredProperties() {
        val config =
            parseString(
                """
                engine=POSTGRES
                host=foobar
                port=1234
                name=baz
                userName=foo
                password=bar
                """.trimIndent(),
            )
        val actual = Configuration.fromConfig(config)
        val expected = Configuration(Engine.POSTGRES, "foobar", 1234, "baz", "foo", "bar")
        assertEquals(expected, actual)
    }

    @Test(expected = ConfigurationException::class)
    fun testFromConfigMissingRequiredProperties() {
        val config =
            parseString(
                """
                port=1234
                name=baz
                userName=foo
                password=bar
                """.trimIndent(),
            )
        val actual = Configuration.fromConfig(config)
        val expected = Configuration(Engine.POSTGRES, "foobar", 1234, "baz", "foo", "bar")
        assertEquals(expected, actual)
    }

    @Test
    fun testConfigToStringHidesPassword() {
        val config =
            parseString(
                """
                engine=POSTGRES
                host=foobar
                port=1234
                name=baz
                userName=foo
                password=bar
                """.trimIndent(),
            )
        val dbConfig = Configuration.fromConfig(config)
        val actual = "$dbConfig"
        val expected = "Connection(engine=POSTGRES, host=foobar, port=1234, database=baz, user=foo, password=****, maxConnections=${dbConfig.maxConnections}, connectionTimeoutMS=${dbConfig.connectionTimeoutMS})"
        assertEquals(expected, actual)
    }
}
