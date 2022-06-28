import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Base64

plugins {
    base
    java
    kotlin("jvm") apply false
    kotlin("kapt") apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

val versionNumber = System.getenv("VERSION")?.substringAfter("R-") ?: "0.0.6"

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
            languageVersion = "1.6"
            apiVersion = "1.6"
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
