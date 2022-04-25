plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":transport"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}")

    testImplementation(http4k("core"))
    testImplementation(http4k("client-jetty"))
}