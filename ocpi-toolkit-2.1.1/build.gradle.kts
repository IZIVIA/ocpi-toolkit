plugins {
    kotlin("jvm")
    id("io.github.4sh.apt.partial")
    id("maven-publish")
}

dependencies {
    implementation(project(":common"))
    api(project(":transport"))

    api("org.apache.logging.log4j:log4j-api:${Versions.log4j}")
    api("org.apache.logging.log4j:log4j-core:${Versions.log4j}")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Versions.jackson}")
    implementation("org.valiktor:valiktor-core:${Versions.valiktor}")

    testImplementation(http4k("core"))
    testImplementation(http4k("contract"))
    testImplementation(http4k("client-jetty"))
    testImplementation(http4k("server-netty"))

    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit}")
    testImplementation("io.strikt:strikt-core:${Versions.strikt}")
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
    java.srcDirs("src/main/kotlinGen")
}

val repo4shUser: String by project
val repo4shPassword: String by project

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "ocpi-2-1-1"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "libs-release-local"
            url = uri("https://repo.4sh.fr/artifactory/libs-release-local")
            credentials {
                username = repo4shUser
                password = repo4shPassword
            }
        }
    }
}