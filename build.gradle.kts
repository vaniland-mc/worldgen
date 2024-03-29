import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"

    id("org.jetbrains.kotlinx.kover") version "0.7.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.0"

    id("com.github.johnrengelman.shadow") version "7.1.2"

    id("io.papermc.paperweight.userdev") version "1.5.5"
}

group = "land.vani.plugin"
version = "2.1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
        content {
            includeGroup("io.papermc.paper")
            includeGroup("com.mojang")
            includeGroup("net.md-5")
        }
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")

    paperweight.paperDevBundle("1.19.3-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-extra-kotlin:4.14.0") {
        exclude("net.kyori")
    }

    implementation("land.vani.mcorouhlin:mcorouhlin-api:7.0.42")
    implementation("land.vani.mcorouhlin:mcorouhlin-paper:7.0.42")
//    implementation("land.vani.mcorouhlin:mcorouhlin-api:SNAPSHOT")
//    implementation("land.vani.mcorouhlin:mcorouhlin-paper:SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
}

val targetJavaVersion = 17
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
        @Suppress("UnstableApiUsage")
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
        @Suppress("UnstableApiUsage")
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            this.jvmTarget = "$targetJavaVersion"
        }
    }

    processResources {
        expand("version" to version)
    }

    withType<Test> {
        useJUnitPlatform()
    }

    withType<Detekt> {
        jvmTarget = "$targetJavaVersion"
        reports {
            xml.required.set(true)
            sarif.required.set(true)
        }
    }

    assemble {
        dependsOn("reobfJar")
    }
}
