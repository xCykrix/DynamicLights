plugins {
    id("io.github.goooler.shadow") version "8.1.8"
    id("java")
    id("java-library")
}

group = "github.xCykrix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    // Core Repositories
    maven {
        url = uri("https://jitpack.io/")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    // Spigot Repository
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    // Flatdir
    flatDir {
        dirs("../SpigotDevkit/build/libs")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    implementation("github.xCykrix:SpigotDevkit:1.0-0-SNAPSHOT-all")
}

// Target Java Build (Java 17 - Minecraft 1.17.x)
val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}
