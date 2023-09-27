buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.8.20"))
    }
}

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.8.20"))
}

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "com.izivia.ocpi.toolkit.processor"
            implementationClass = "com.izivia.ocpi.toolkit.processor.AptGradlePlugin"
        }
    }
}
