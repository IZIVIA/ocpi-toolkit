plugins {
    base
    `java-library`
    kotlin("kapt")
}

kotlinProject()

publishing {
    publications {
        named<MavenPublication>("maven") {
            groupId = "com.izivia"
            artifactId = "ocpi-annotation-processor"
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
