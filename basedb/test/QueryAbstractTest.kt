package com.suryadigital.leo.basedb

import com.suryadigital.leo.testUtils.runWithKtorMetricsContext
import org.junit.AfterClass
import org.junit.BeforeClass
import org.testcontainers.containers.PostgreSQLContainer

abstract class QueryAbstractTest {
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
            val configuration = Configuration(engine = Engine.POSTGRES, host = "localhost", port = port, name = dbname, password = password, userName = username)
            database = Database(configuration = configuration)
            val insertSQL: String =
                """CREATE TABLE "test"(
            | "id" INT PRIMARY KEY,
            | "name" VARCHAR(10) NOT NULL,
            | "isActive" BOOLEAN DEFAULT TRUE
            |);
            | INSERT INTO "test" VALUES (1, 'TEST1');
            | INSERT INTO "test" VALUES (2, 'TEST2');
            | INSERT INTO "test" VALUES (3, 'TEST3');
            | INSERT INTO "test" VALUES (4, 'TEST4');
            | INSERT INTO "test" VALUES (5, 'TEST5');
            | INSERT INTO "test" VALUES (6, 'TEST6');
            | INSERT INTO "test" VALUES (7, 'TEST7');
                """.trimMargin()

            runWithKtorMetricsContext {
                database.timedQuery {
                    it.execute(insertSQL)
                }
            }
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            database.close()
            postgresContainer.stop()
        }
    }
}
