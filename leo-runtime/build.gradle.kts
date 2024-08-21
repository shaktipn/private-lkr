import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.spotless)
    alias(libs.plugins.shadow)
}

tasks.withType<ShadowJar> {
    archiveBaseName = "leo-runtime"
    archiveClassifier = ""
    archiveVersion = ""
    /**
     * `isZip6` is required since the bundle size has become large.
     * Relevant discussion can be found here: https://github.com/johnrengelman/shadow/issues/107#issuecomment-66461675
     */
    isZip64 = true
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
    api(project(":aws-kt"))
    api(project(":basedb"))
    api(project(":crypto"))
    api(project(":distributed-sync-store"))
    api(project(":eagle-runtime"))
    api(project(":file-service-jooq"))
    api(project(":firebase-kt"))
    api(project(":inline-logger"))
    api(project(":jooq"))
    api(project(":kedwig-core"))
    api(project(":kedwig-jvm"))
    api(project(":kotlinx-serialization-json"))
    api(project(":kt-utils"))
    api(project(":ktor"))
    api(project(":librpc"))
    api(project(":plugins"))
    api(project(":rate-limiter"))
    api(project(":storage"))
    api(project(":table-runtime"))
    api(project(":test-utils"))
    api(project(":types"))
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            project.shadow.component(this)
        }
    }
}
