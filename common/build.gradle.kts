plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
}

val repo4shUser: String by project
val repo4shPassword: String by project

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "ocpi-common"
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
