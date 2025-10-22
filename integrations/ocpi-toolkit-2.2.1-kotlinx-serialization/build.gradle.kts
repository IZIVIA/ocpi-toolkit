plugins {
    `public-lib`
    kotlin("plugin.serialization") version Versions.kotlinxSerializationPlugin
    id("java-library")
    id("com.google.devtools.ksp")
}

dependencies {
    internal(project(":annotation-processor"))
    ksp(project(":annotation-processor"))

    implementation(project(":ocpi-toolkit-2.2.1"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationDependency}")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            artifactId = "ocpi-2-2-1-kotlinx-serialization"
            from(components["java"])

            pom {
                name.set("OCPI 2.2.1 kotlinx.kotlinx-serialization")
                artifactId = "ocpi-2-2-1-kotlinx-serialization"
                description.set("Kotlinx Serialization serializers for OCPI data classes")
            }
        }
    }
}
