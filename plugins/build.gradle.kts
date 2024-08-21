plugins {
    id("java-gradle-plugin")
    id("groovy")
    alias(libs.plugins.spotless)
}

gradlePlugin {
    plugins {
        create("renderMjmlTemplate") {
            id = "renderMjmlTemplate"
            implementationClass = "com.suryadigital.leo.plugins.RenderMjmlTemplate"
            group = group
            version = version
        }
        create("migrateFileServiceSchema") {
            id = "migrateFileServiceSchema"
            implementationClass = "com.suryadigital.leo.plugins.MigrateFileServiceSchema"
            group = group
            version = version
        }
    }
}

dependencies {
    implementation(libs.freemarker)
    implementation(libs.postgresql)
    implementation(libs.flyway.core)
}

sourceSets {
    main {
        groovy { srcDirs("src") }
        resources { srcDirs("resources") }
    }
    test {
        groovy { srcDirs("test") }
        resources { srcDirs("testresources") }
    }
}

spotless {
    kotlin {
        ktlint().setEditorConfigPath("$rootDir/.editorconfig")
    }
}

publishing {
    repositories {
        maven {
            url = uri("${layout.buildDirectory}/repo")
        }
    }
}
