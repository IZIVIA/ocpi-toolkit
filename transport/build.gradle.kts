plugins {
    kotlin("jvm")
    id("maven-publish")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            artifactId = "ocpi-transport"
            from(components["java"])

            pom {
                name.set("OCPI Transport")
                artifactId = "ocpi-transport"
                description.set("This module hosts all transport implementations")
            }
        }
    }
}
