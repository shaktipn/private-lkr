package com.suryadigital.leo.plugins

import org.flywaydb.core.Flyway
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin to perform database migration for `file-service` schema, required by File Service feature in Eagle-Gen.
 */
class MigrateFileServiceSchema implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def databaseConfiguration = project.extensions.create("fileServiceSchemaDatabaseConfiguration", FileServiceSchemaDatabaseConfiguration)

        project.tasks.register("migrateFileServiceSchema") {
            doLast {
                Flyway.configure()
                        .dataSource(
                                "jdbc:postgresql://${databaseConfiguration.host}:${databaseConfiguration.port}/${databaseConfiguration.name}",
                                databaseConfiguration.username,
                                databaseConfiguration.password,
                        )
                        .schemas("file-service")
                        .locations("classpath:file-service-migration")
                        .load()
                        .migrate()
            }
        }
    }
}

/**
 * Configuration parameters based on which database connection will be established to the database on which migration needs to be performed.
 *
 * @property host hostname for the database.
 * @property port port at which database is listening for connection.
 * @property name name of the database.
 * @property username name of the user, using which we can authenticate the connection to the database.
 * @property password password of the user to authenticate connection to the database.
 */
class FileServiceSchemaDatabaseConfiguration {
    String host
    Integer port
    String name
    String username
    String password
}
