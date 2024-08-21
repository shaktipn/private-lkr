package com.suryadigital.leo.jooq

import org.flywaydb.core.Flyway
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generate
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Strategy
import org.jooq.meta.jaxb.Target
import org.junit.AfterClass
import org.junit.BeforeClass
import org.testcontainers.containers.PostgreSQLContainer

internal abstract class AbstractTest {
    companion object {
        private val postgreSQLContainer = PostgreSQLContainer("postgres:15.5")

        @BeforeClass
        @JvmStatic
        fun setup() {
            postgreSQLContainer.start()
            Flyway.configure()
                .cleanDisabled(false)
                .dataSource(
                    postgreSQLContainer.jdbcUrl,
                    postgreSQLContainer.username,
                    postgreSQLContainer.password,
                )
                .schemas("public")
                .locations("filesystem:${System.getProperty("user.dir")}/testresources/migration")
                .load()
                .migrate()
            GenerationTool.generate(
                Configuration()
                    .withJdbc(
                        Jdbc()
                            .withDriver("org.postgresql.Driver")
                            .withUrl(postgreSQLContainer.jdbcUrl)
                            .withUser(postgreSQLContainer.username)
                            .withPassword(postgreSQLContainer.password),
                    )
                    .withGenerator(
                        Generator()
                            .withGenerate(
                                Generate()
                                    // DAOs and Interfaces are generated in addition to records to increase the coverage.
                                    .withDaos(true)
                                    .withImmutableInterfaces(true),
                            )
                            .withName("org.jooq.codegen.KotlinGenerator")
                            .withDatabase(
                                Database()
                                    .withInputSchema("public"),
                            )
                            .withStrategy(Strategy().withName("com.suryadigital.leo.jooq.CamelCaseNameGeneratorStrategy"))
                            .withTarget(
                                Target()
                                    .withPackageName("com.suryadigital.leo.jooq.generatedCode")
                                    .withDirectory(this::class.java.getResource("/")?.path)
                                    .withClean(true),
                            ),
                    ),
            )
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            postgreSQLContainer.stop()
        }
    }
}
