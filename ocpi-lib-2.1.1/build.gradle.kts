plugins {
    kotlin("jvm")
    id("io.github.4sh.apt.partial")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":transport"))

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

    testRuntimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
