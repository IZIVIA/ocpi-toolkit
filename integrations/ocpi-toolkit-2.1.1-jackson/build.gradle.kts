plugins {
    `public-lib`
}

dependencies {
    api(project(":ocpi-toolkit-2.1.1"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Versions.jackson}")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            artifactId = "ocpi-2-1-1-jackson"
            from(components["java"])

            pom {
                name.set("OCPI 2.1.1 jackson serialization")
                artifactId = "ocpi-2-1-1-jackson"
                description.set("Jackson serializers for OCPI 2.1.1 data classes")
            }
        }
    }
}
