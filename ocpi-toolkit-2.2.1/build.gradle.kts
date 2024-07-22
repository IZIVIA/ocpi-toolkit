plugins {
    id("java-library")
    id("com.google.devtools.ksp")
    `public-lib`
}

dependencies {
    implementation(project(":annotation-processor"))
    ksp(project(":annotation-processor"))
    api(project(":transport"))

    api("org.apache.logging.log4j:log4j-api:${Versions.log4j}")
    api("org.apache.logging.log4j:log4j-core:${Versions.log4j}")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Versions.jackson}")
    implementation("org.valiktor:valiktor-core:${Versions.valiktor}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")

    testImplementation(http4k("core"))
    testImplementation(http4k("contract"))
    testImplementation(http4k("client-jetty"))
    testImplementation(http4k("server-netty"))

    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit}")
    testImplementation("io.strikt:strikt-core:${Versions.strikt}")
    testImplementation("com.github.fslev:json-compare:6.10")
    testImplementation("io.mockk:mockk:${Versions.mockk}")

    testImplementation("org.testcontainers:testcontainers:${Versions.testcontainers}")
    testImplementation("org.testcontainers:junit-jupiter:${Versions.testcontainers}")
    testImplementation("org.testcontainers:mongodb:${Versions.testcontainers}")
    testImplementation("org.litote.kmongo:kmongo:${Versions.kmongo}")

    testRuntimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

sourceSets.main {
    // new way to use buildDir: https://docs.gradle.org/current/userguide/upgrading_version_8.html#deprecations_3
    kotlin.srcDirs("${layout.buildDirectory.get().asFile.absolutePath}/generated/ksp")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            artifactId = "ocpi-2-2-1"
            groupId = "com.izivia"

            from(components["java"])

            pom {
                name.set("OCPI 2.2.1")
                artifactId = "ocpi-2-2-1"
                description.set("This module implements the v2.2.1 of the OCPI spec")
            }
        }
    }
}
