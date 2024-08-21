package com.suryadigital.leo.ktor.tests

import com.suryadigital.leo.ktor.parseConfig
import com.typesafe.config.ConfigFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ConfigParsingTest {
    private val confPath = ConfigParsingTest::class.java.getResource("/application.conf")?.path

    @Test
    fun testParseConfigWithNoArguments() {
        val args = arrayOf<String>()
        val config = parseConfig(args)
        val fileConfig = ConfigFactory.load()
        assertNotNull(config)
        assertEquals(fileConfig.resolve(), config)
    }

    @Test
    fun testParseConfigWithCommandlineArguments() {
        val args = arrayOf("-config=$confPath", "-P:db.url=jdbc:sqlite:test.db")
        val config = parseConfig(args)
        assertEquals("jdbc:sqlite:test.db", config.getString("db.url"))
    }

    @Test
    fun testParseConfigPriorityCommandlineOverFile() {
        val args = arrayOf("-config=$confPath", "-P:ktor.port=9090")
        val config = parseConfig(args)
        assertEquals(9090, config.getInt("ktor.port"))
    }

    @Test
    fun testparseConfigWithMissingConfigFile() {
        val args = arrayOf("-P:missing.property=default")
        val config = parseConfig(args)
        assertEquals("default", config.getString("missing.property"))
    }
}
