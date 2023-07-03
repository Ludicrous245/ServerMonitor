import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.8.21"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

val pluginName = "ServerMonitor"
val paperMC = "io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

allprojects{
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories{
        mavenCentral()

        maven("https://jitpack.io")

        maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    }

    dependencies{
        implementation(kotlin("stdlib"))

        implementation("com.google.code.gson:gson:2.9.0")
        compileOnly(paperMC)

        implementation("im.kimcore:Josa.kt:1.6")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

project.extra.set("pluginName", name.split('-').joinToString("") { it.capitalize() })

tasks {
    named<ShadowJar>("shadowJar") {
        dependencies {
            exclude(paperMC)
        }

        archiveBaseName.set(pluginName)

        mergeServiceFiles()
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}