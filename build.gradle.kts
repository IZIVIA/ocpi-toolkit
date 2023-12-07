import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.util.Base64

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
    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
            languageVersion = "1.8"
            apiVersion = "1.8"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("maven-publish")
        plugin("signing")
        plugin("org.jlleitschuh.gradle.ktlint")
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

    extensions.getByType<PublishingExtension>().publications {
        create<MavenPublication>("maven") {
            pom {
                url.set("https://github.com/IZIVIA/ocpi-toolkit")

                scm {
                    connection.set("scm:git:https://github.com/IZIVIA/ocpi-toolkit.git")
                    developerConnection.set("scm:git:git@github.com:IZIVIA/ocpi-toolkit.git")
                    url.set("https://github.com/IZIVIA/ocpi-toolkit")
                }

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("IZIVIA")
                        name.set("IZIVIA")
                        email.set("izivia-fr-dms-deliv@izivia.com")
                        url.set("https://www.izivia.com/")
                        organization.set("IZIVIA")
                        organizationUrl.set("https://www.izivia.com/")
                    }
                }
            }
        }
    }

    extensions.getByType<SigningExtension>()
        .sign(extensions.getByType<PublishingExtension>().publications.named("maven").get())
    if (System.getenv("GPG_PRIVATE_KEY") != null) {
        extensions.getByType<SigningExtension>().useInMemoryPgpKeys(
            Base64.getDecoder().decode(System.getenv("GPG_PRIVATE_KEY")).decodeToString(),
            System.getenv("GPG_PASSPHRASE")
        )
    }

    tasks.withType<Sign> {
        onlyIf { System.getenv("GPG_PRIVATE_KEY") != null }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("SONATYPE_USERNAME"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
        }
    }
}
