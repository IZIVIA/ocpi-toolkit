plugins {
    id("java-library")
    id("com.google.devtools.ksp")
    `public-lib`
}

dependencies {
    internal(project(":annotation-processor"))
    ksp(project(":annotation-processor"))
    api(project(":transport"))

    api("org.apache.logging.log4j:log4j-api:${Versions.log4j}")
    api("org.apache.logging.log4j:log4j-core:${Versions.log4j}")

    implementation("org.valiktor:valiktor-core:${Versions.valiktor}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")

    testImplementation(http4k("core"))
    testImplementation(http4k("api-openapi"))
    testImplementation(http4k("client-okhttp"))
    testImplementation(http4k("server-netty"))

    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit}")
    testImplementation("io.strikt:strikt-core:${Versions.strikt}")
    testImplementation("io.github.deblockt:json-diff:${Versions.jsonDiff}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")

    testImplementation("org.testcontainers:testcontainers:${Versions.testcontainers}")
    testImplementation("org.testcontainers:junit-jupiter:${Versions.testcontainers}")
    testImplementation("org.testcontainers:mongodb:${Versions.testcontainers}")
    testImplementation("org.litote.kmongo:kmongo:${Versions.kmongo}")

    testImplementation(project(":integrations:ocpi-toolkit-2.2.1-jackson"))
    testImplementation(project(":integrations:ocpi-toolkit-2.2.1-kotlinx-serialization"))
    testRuntimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
