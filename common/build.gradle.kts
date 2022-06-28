
java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            artifactId = "ocpi-common"
            from(components["java"])

            pom {
                name.set("OCPI Common")
                artifactId = "ocpi-common"
                description.set("This module provides some common features")
            }
        }
    }
}
