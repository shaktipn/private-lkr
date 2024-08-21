rootProject.name = "leo-kotlin-runtime"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include("aws-kt")
include("basedb")
include("crypto")
include("distributed-sync-store")
include("eagle-runtime")
include("file-service-jooq")
include("firebase-kt")
include("inline-logger")
include("jooq")
include("kedwig-core")
include("kedwig-jvm")
include("kotlinx-serialization-json")
include("kt-utils")
include("ktor")
include("leo-runtime")
include("librpc")
include("plugins")
include("rate-limiter")
include("storage")
include("table-runtime")
include("test-utils")
include("types")
