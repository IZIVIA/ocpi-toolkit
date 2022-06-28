plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            artifactId = "ocpi-transport"
            from(components["java"])
        }
    }
}
