plugins {
    base
    java
    `java-library`
    kotlin("jvm")
    kotlin("kapt")
    id("maven-publish")
}

kotlinProject()

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "io.github.4sh"
            artifactId = "partial-annotation-processor"
            version = "1.0.0"
            from(components["java"])
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
