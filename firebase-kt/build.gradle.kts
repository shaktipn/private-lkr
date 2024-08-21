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
    implementation(project(":kedwig-jvm"))
    implementation(project(":kotlinx-serialization-json"))
    implementation(project(":kt-utils"))
    implementation(project(":test-utils"))
    implementation(libs.coroutines)
    implementation(libs.java.jwt)
    implementation(libs.koin.ktor)
    implementation(libs.koin.sl4j)
    implementation(libs.kotlinx.serialization)
    implementation(libs.slf4j.api)
    testImplementation(libs.kotlin.test)
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
