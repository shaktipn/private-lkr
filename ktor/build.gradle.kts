plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kotlinx.serialization)
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
    implementation(project(":inline-logger"))
    implementation(project(":librpc"))
    implementation(libs.koin.ktor)
    implementation(libs.kotlinx.serialization)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.call.id)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.forwarded.header)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.typesafe.config)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.server.test.host)
}

spotless {
    kotlin {
        ktlint().setEditorConfigPath("$rootDir/.editorconfig")
    }
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
