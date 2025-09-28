import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
    }
}

plugins {
    kotlin("jvm") version Versions.kotlin apply false
    id("com.google.devtools.ksp") version Versions.ksp apply false
    id("org.jlleitschuh.gradle.ktlint") version Versions.ktlintPlugin apply false
    id("io.github.gradle-nexus.publish-plugin") version Versions.nexus
}

val versionNumber = System.getenv("VERSION")?.substringAfter("R-") ?: "0.0.15"

println("building current version: $versionNumber")

allprojects {
    group = "com.izivia"
    version = versionNumber

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.named<KotlinJvmCompile>("compileKotlin"){
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(Versions.jvm)
            freeCompilerArgs = listOf("-Xjsr305=strict")
            languageVersion = KotlinVersion.KOTLIN_2_2
            apiVersion = KotlinVersion.KOTLIN_2_2
        }
    }

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(Versions.jvm))
        }
    }

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set(Versions.ktlint)
        verbose.set(true)
        outputToConsole.set(true)
        reporters {
            reporter(ReporterType.JSON)
        }
        filter {
            exclude { element -> element.file.path.contains("generated") }
        }
    }

    plugins.withType<JavaLibraryPlugin> {
        val internal by configurations.creating {
            isVisible = false
            isCanBeConsumed = false
            isCanBeResolved = false
        }
        configurations["compileClasspath"].extendsFrom(internal)
        configurations["runtimeClasspath"].extendsFrom(internal)
        configurations["testCompileClasspath"].extendsFrom(internal)
        configurations["testRuntimeClasspath"].extendsFrom(internal)
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(System.getenv("SONATYPE_USERNAME"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
        }
    }
}
