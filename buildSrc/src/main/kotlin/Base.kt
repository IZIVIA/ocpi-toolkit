import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.repositories

fun http4k(module:String) = "org.http4k:http4k-${module}:${Versions.http4k}"

fun Project.kotlinProject() {
    dependencies {
        "implementation"(kotlin("stdlib-jdk8"))
        "implementation"(kotlin("reflect"))
    }
    repositories {
        mavenCentral()
    }
}
