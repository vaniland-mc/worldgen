import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.jpenilla.specialgradle.task.RemapJar

plugins {
    kotlin("jvm") version "1.5.31"
    id("xyz.jpenilla.special-gradle") version "1.0.0-SNAPSHOT"
}

group = "land.vani"
version = "0.1.0"

repositories {
    mavenCentral()

    maven {
        name = "spigotmc-repo"
        setUrl("https://hub.spigotmc.org/nexus/content/repositories/public/")
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

val targetJavaVersion = 16
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if(JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

specialGradle {
    // set Minecraft version for running BuildTools and injecting Spigot dependency
    minecraftVersion.set("1.17.1")
    // set SpecialSource version
    specialSourceVersion.set("1.10.0")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "$targetJavaVersion"
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    build {
        dependsOn(productionMappedJar)
    }

    buildTools {
        quiet.set(true)
    }
    withType<RemapJar> {
        quiet.set(true)
    }

    processResources {
        include("plugin.yml")
        expand("version" to version)
    }
}
