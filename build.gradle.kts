plugins {
    id("io.github.goooler.shadow") version "8.1.8"
    id("java")
    id("java-library")
}

group = "github.xCykrix"
version = "1.1.0"

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

    // Upstream GitHub Packages
    maven {
        url = uri("https://maven.pkg.github.com/xCykrix/SpigotDevkit")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    implementation("github.xCykrix:spigotdevkit:1.0.1")
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
