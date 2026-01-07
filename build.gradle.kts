import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("io.github.goooler.shadow") version "8.1.8"
    id("java")
    id("java-library")
}

group = "github.xCykrix"
version = "2.0.0-MC1.21.11-PAPER"

repositories {
    mavenCentral()

    // Upstream Repository
    maven("https://repo.papermc.io/repository/maven-public/")

    // Upstream GitHub Packages
    maven {
        url = uri("https://maven.pkg.github.com/xCykrix/SpigotDevkit")
        credentials {
            username = project.findProperty("GITHUB_ACTOR").toString() ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("GITHUB_TOKEN").toString() ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("github.xCykrix:spigotdevkit:MC1.21.11-R0.8:paper") {
        isTransitive = false
    }
}

// Shadow Task
tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier = null;
      manifest {
    attributes["paperweight-mappings-namespace"] = "mojang"
  }
}

// Target Java Build (Java 21)
val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}
