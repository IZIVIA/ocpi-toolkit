buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.6.10"))
    }
}

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.6.10"))
}

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "com.izivia.ocpi.toolkit.processor"
            implementationClass = "com.izivia.ocpi.toolkit.processor.AptGradlePlugin"
        }
    }
}
