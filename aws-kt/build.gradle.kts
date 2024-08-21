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
    implementation(project(":kt-utils"))
    implementation(project(":test-utils"))
    implementation(project(":types"))
    implementation(libs.awssdk.netty.nio.client)
    implementation(libs.awssdk.s3)
    implementation(libs.awssdk.secretsmanager)
    implementation(libs.awssdk.ses)
    implementation(libs.awssdk.sns)
    implementation(libs.coroutines)
    implementation(libs.koin.ktor)
    implementation(libs.koin.sl4j)
    implementation(libs.kotlinx.serialization)
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)
    implementation(libs.typesafe.config)
}

spotless {
    kotlin {
        ktlint().setEditorConfigPath("$rootDir/.editorconfig")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
