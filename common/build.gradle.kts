publishing {
    publications {
        named<MavenPublication>("maven") {
            artifactId = "ocpi-common"
            from(components["java"])
        }
    }
}
