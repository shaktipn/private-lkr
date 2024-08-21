package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.math.abs

abstract class JooqUtilsAbstractTest {
    companion object {
        private lateinit var postgresContainer: PostgreSQLContainer<Nothing>
        lateinit var database: Database

        @BeforeClass
        @JvmStatic
        fun setup() {
            postgresContainer = PostgreSQLContainer<Nothing>("postgres:12.4")
            postgresContainer.start()
            val dbname = postgresContainer.databaseName
            val port = postgresContainer.firstMappedPort
            val username = postgresContainer.username
            val password = postgresContainer.password
            val configuration = Configuration(Engine.POSTGRES, "localhost", port = port, name = dbname, password = password, userName = username)
            database = Database(configuration)
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            database.close()
            postgresContainer.stop()
        }
    }

    @Before
    fun createTable() {
        val insertSQL =
            """CREATE TABLE "test"(
            | "integerColumn" INT DEFAULT NULL,
            | "longColumn" BIGINT DEFAULT NULL,
            | "booleanColumn" BOOLEAN DEFAULT NULL,
            | "stringColumn" VARCHAR(10) DEFAULT NULL,
            | "floatColumn" REAL DEFAULT NULL,
            | "doubleColumn" DOUBLE PRECISION DEFAULT NULL,
            | "decimalColumn" DECIMAL DEFAULT NULL
            |); 
            """.trimMargin()

        runWithKtorMetricsContext {
            database.timedQuery(TransactionIsolationLevel.SERIALIZABLE) {
                it.execute(insertSQL)
            }
        }
    }

    @After
    fun deleteTable() {
        runWithKtorMetricsContext {
            database.timedQuery(TransactionIsolationLevel.SERIALIZABLE) {
                it.dropTable("test").execute()
            }
        }
    }

    /**
     * Compares two Float values for equality. Returns true if the absolute value of the difference between two float values is less
     * than 0.000001 (epsilon) else false.
     *
     * @param value1 Float variable one.
     * @param value2 Float variable two.
     * @param epsilon Epsilon value.
     * @return true if equal, else false.
     */
    internal fun compareFloatValuesForEquality(
        value1: Float,
        value2: Float,
        epsilon: Float = 0.000001f,
    ): Boolean {
        val absoluteValue = abs((value1 - value2))
        return (absoluteValue < epsilon)
    }

    // Needs to be removed after update to kotlin 1.5. Refer: https://kotlinlang.org/api/latest/kotlin.test/kotlin.test/assert-equals.html

    /**
     * Compares two Double values for equality. Returns true if the absolute value of the difference between two Double values is less
     * than 0.000001 (epsilon) else false.
     *
     * @param value1 Double variable one.
     * @param value2 Double variable two.
     * @param epsilon Epsilon value.
     * @return true if equal, else false.
     */
    internal fun compareDoubleValuesForEquality(
        value1: Double,
        value2: Double,
        epsilon: Double = 0.000001,
    ): Boolean {
        val absoluteValue = abs((value1 - value2))
        return (absoluteValue < epsilon)
    }
}
