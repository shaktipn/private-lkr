import java.util.Properties

buildscript {
    repositories(RepositoryHandler::mavenCentral)
    dependencies {
        classpath(libs.kotlin.gradle)
        classpath(libs.build.info.extractor.gradle)
    }
}

plugins {
    // This is required to make sure that the kotlin-jvm plugin does not get added to the classpath by some other plugin, and we only use the version defined in `libs.version.toml` in every module.
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.versions)
    alias(libs.plugins.versions.catalog.update)
    id("version-catalog")
    id("maven-publish")
}

/**
 * [Properties] instance to load the credentials for private artifactory repository from a properties file.
 */
val artifactoryProperties: Properties = Properties()
artifactoryProperties.load(java.io.FileInputStream(File("$rootDir/artifactory.properties")))

subprojects {
    buildscript {
        repositories(RepositoryHandler::mavenCentral)
    }
    apply(plugin = "maven-publish")
    repositories {
        mavenCentral()
        maven {
            setUrl("https://artifacts.surya-digital.in/repository/maven-releases/")
            credentials {
                username = artifactoryProperties["username"].toString()
                password = artifactoryProperties["password"].toString()
            }
        }
    }
    publishing {
        repositories {
            maven {
                setUrl("https://artifacts.surya-digital.in/repository/maven-releases/")
                credentials {
                    username = artifactoryProperties["username"].toString()
                    password = artifactoryProperties["password"].toString()
                }
            }
        }
    }
}

/**
 * Fetch the next build version from the environment variables, or use `SNAPSHOT`.
 */
val leoVersion: String = (System.getenv("NEXT_BUILD_VERSION") ?: "SNAPSHOT")

/**
 * Package group under which the packages should be published.
 */
val leoGroup: String = "com.suryadigital.leo"

versionCatalogUpdate {
    keep {
        // keep versions without any library or plugin reference
        keepUnusedVersions = true
        // keep all libraries that aren't used in the project
        keepUnusedLibraries = true
        // keep all plugins that aren't used in the project
        keepUnusedPlugins = true
    }
}

catalog {
    versionCatalog {
        version("leo", leoVersion)
        library("leo-aws-kt", leoGroup, "aws-kt").versionRef("leo")
        library("leo-basedb", leoGroup, "basedb").versionRef("leo")
        library("leo-crypto", leoGroup, "crypto").versionRef("leo")
        library("leo-distributed-sync-store", leoGroup, "distributed-sync-store").versionRef("leo")
        library("leo-eagle-runtime", leoGroup, "eagle-runtime").versionRef("leo")
        library("leo-fileServiceJooq", leoGroup, "file-service-jooq").versionRef("leo")
        library("leo-firebase-kt", leoGroup, "firebase-kt").versionRef("leo")
        library("leo-inline-logger", leoGroup, "inline-logger").versionRef("leo")
        library("leo-jooq", leoGroup, "jooq").versionRef("leo")
        library("leo-kedwig-core", leoGroup, "kedwig-core").versionRef("leo")
        library("leo-kedwig-jvm", leoGroup, "kedwig-jvm").versionRef("leo")
        library("leo-kotlinx-serialization-json", leoGroup, "kotlinx-serialization-json").versionRef("leo")
        library("leo-kt-utils", leoGroup, "kt-utils").versionRef("leo")
        library("leo-ktor", leoGroup, "ktor").versionRef("leo")
        library("leo-librpc", leoGroup, "librpc").versionRef("leo")
        library("leo-rate-limiter", leoGroup, "rate-limiter").versionRef("leo")
        library("leo-runtime", leoGroup, "leo-runtime").versionRef("leo")
        library("leo-storage", leoGroup, "storage").versionRef("leo")
        library("leo-table-runtime", leoGroup, "table-runtime").versionRef("leo")
        library("leo-test-utils", leoGroup, "test-utils").versionRef("leo")
        library("leo-types", leoGroup, "types").versionRef("leo")
        plugin("leo-plugin", "$leoGroup.plugins").versionRef("leo")
        from(files("${rootProject.projectDir}/gradle/libs.versions.toml"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["versionCatalog"])
            artifactId = "version-catalog"
            group = leoGroup
            version = leoVersion
        }
    }
    repositories {
        maven {
            setUrl("https://artifacts.surya-digital.in/repository/maven-releases/")
            credentials {
                username = artifactoryProperties["username"].toString()
                password = artifactoryProperties["password"].toString()
            }
        }
    }
}

allprojects {
    group = leoGroup
    version = leoVersion
}
