plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "ocpi-common"
            from(components["java"])
        }
    }
}
