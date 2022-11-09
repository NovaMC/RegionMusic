import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    signing
    `maven-publish`
    java
    kotlin("jvm") version "1.7.20"
}

group = "xyz.novaserver"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.novaserver.xyz/snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.10-SNAPSHOT")
    compileOnly("xyz.novaserver.core:core-paper:1.0-SNAPSHOT")
}

publishing {
    repositories {
        maven {
            name = "novaReleases"
            url = uri("https://repo.novaserver.xyz/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven {
            name = "novaSnapshots"
            url = uri("https://repo.novaserver.xyz/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String?
            artifactId = project.name
            version = project.version as String?
            from(components["java"])
        }
    }
}

tasks {
    jar {
        archiveFileName.set("${project.name}-${project.version}.jar")
    }
    build {
        dependsOn(jar)
    }
}

val targetJavaVersion = 17

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = targetJavaVersion.toString()
}
