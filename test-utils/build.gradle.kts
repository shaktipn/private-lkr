plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.spotless)
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
    implementation(project(":kedwig-jvm"))
    implementation(project(":kotlinx-serialization-json"))
    implementation(project(":librpc"))
    implementation(project(":ktor"))
    implementation(libs.coroutines)
    implementation(libs.kotlin.test)
    implementation(libs.kotlinx.serialization)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.kotlinx.serialization)
    testImplementation(libs.ktor.server.call.logging)
    testImplementation(libs.ktor.server.netty)
    testImplementation(libs.slf4j.simple)
    testImplementation(libs.okhttp)
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
