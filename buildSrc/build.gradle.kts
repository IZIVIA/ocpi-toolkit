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
            id = "io.github.4sh.apt.partial"
            implementationClass = "io.github.quatresh.PartialAptGradlePlugin"
        }
    }
}
