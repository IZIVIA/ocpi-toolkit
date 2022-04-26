plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":transport"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Versions.jackson}")
    implementation("org.valiktor:valiktor-core:${Versions.valiktor}")

    testImplementation(http4k("core"))
    testImplementation(http4k("contract"))
    testImplementation(http4k("client-jetty"))
    testImplementation(http4k("server-netty"))

    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit}")
    testImplementation("io.strikt:strikt-core:${Versions.strikt}")

    testRuntimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
