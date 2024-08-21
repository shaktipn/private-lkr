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
    implementation(project(":types"))
    implementation(libs.kotlinx.serialization)
    implementation(libs.libphonenumber)
    testImplementation(libs.kotlin.test)
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
