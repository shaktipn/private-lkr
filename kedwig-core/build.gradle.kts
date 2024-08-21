plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(libs.coroutines)
    implementation(libs.okhttp)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.json.path)
    testImplementation(libs.okhttp.mockserver)
}

tasks {
    withType<Test> {
        useJUnit()
        maxHeapSize = "25M"
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
