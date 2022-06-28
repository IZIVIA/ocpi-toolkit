import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    java
    kotlin("jvm") apply false
    kotlin("kapt") apply false
    id("maven-publish")
}

val versionNumber = System.getenv("version")?.substringAfter("R-") ?: "0.0.6"

allprojects {

    group = "com.izivia"
    version = versionNumber

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
            languageVersion = "1.6"
            apiVersion = "1.6"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        ignoreFailures = true
    }
}
