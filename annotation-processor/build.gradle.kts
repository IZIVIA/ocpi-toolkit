plugins {
    base
    `java-library`
    kotlin("kapt")
}

kotlinProject()

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            groupId = "com.izivia"
            artifactId = "ocpi-annotation-processor"
            version = System.getenv("VERSION")?.substringAfter("R-") ?: "1.0.0"
            from(components["java"])

            pom {
                name.set("OCPI Annotation processor")
                artifactId = "ocpi-annotation-processor"
                description.set("This module processes annotations during compilation time in order to generate some code (partial representation, etc ...)")
            }
        }
    }
}

dependencies {
    implementation("com.squareup:kotlinpoet:${Versions.kotlinPoet}")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    implementation("de.jensklingenberg:mpapt-runtime:${Versions.mpapt}")
}

tasks.build {
    dependsOn(tasks.publishToMavenLocal)
}
