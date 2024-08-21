import org.flywaydb.core.Flyway
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Strategy
import org.jooq.meta.jaxb.Target
import org.testcontainers.containers.PostgreSQLContainer

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
}

buildscript {
    dependencies {
        classpath(libs.jooq.codegen)
        classpath(libs.flyway.core)
        classpath(libs.testcontainers.postgres)
        classpath(libs.postgresql)
    }
}

sourceSets {
    main {
        kotlin { srcDirs("src") }
        resources { srcDirs("resources") }
    }
    test {
        kotlin { srcDirs("test") }
        resources { srcDirs("testresources") }
    }
}

dependencies {
    implementation(libs.jooq)
    implementation(libs.koin.core)
    implementation(project(":basedb"))
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("src/com/suryadigital/leo/fileServiceJooq/generatedCode/**/*.kt")
        ktlint()
            .setEditorConfigPath("$rootDir/.editorconfig")
    }
}

tasks.register("generateJooqCodeForFileService") {
    doLast {
        val postgreSQLContainer = PostgreSQLContainer("postgres:15.5")
        postgreSQLContainer.start()
        Flyway.configure()
            .cleanDisabled(false)
            .dataSource(
                postgreSQLContainer.jdbcUrl,
                postgreSQLContainer.username,
                postgreSQLContainer.password,
            )
            .schemas("file-service")
            .locations("filesystem:plugins/resources/file-service-migration")
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
                        .withName("org.jooq.codegen.KotlinGenerator")
                        .withDatabase(
                            Database()
                                .withInputSchema("file-service")
                                .withForcedTypes(
                                    ForcedType()
                                        .withName("INSTANT")
                                        .withIncludeTypes("TIMESTAMPTZ|TIMESTAMP WITH TIME ZONE"),
                                ),
                        )
                        // We are not using codegen strategy from jooq module due to build getting stuck after adding the module to classpath.
                        // Relevant ticket can be found here: https://surya-digital.atlassian.net/browse/ST-481
                        .withStrategy(Strategy())
                        .withTarget(
                            Target()
                                .withPackageName("com.suryadigital.leo.fileServiceJooq.generatedCode")
                                .withDirectory("$projectDir/src")
                                .withClean(true),
                        ),
                ),
        )
        postgreSQLContainer.stop()
    }
}

tasks.compileKotlin {
    dependsOn(tasks.named("generateJooqCodeForFileService"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    val sourcesJar: Jar by tasks.creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.getByName("main").allSource)
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}
