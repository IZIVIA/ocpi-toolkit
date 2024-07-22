import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import java.util.*

plugins {
    id("maven-publish")
    id("signing")
}

extensions.getByType<PublishingExtension>().publications {
    create<MavenPublication>("maven") {
        pom {
            url.set("https://github.com/IZIVIA/ocpi-toolkit")

            scm {
                connection.set("scm:git:https://github.com/IZIVIA/ocpi-toolkit.git")
                developerConnection.set("scm:git:git@github.com:IZIVIA/ocpi-toolkit.git")
                url.set("https://github.com/IZIVIA/ocpi-toolkit")
            }

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

            developers {
                developer {
                    id.set("IZIVIA")
                    name.set("IZIVIA")
                    email.set("izivia-fr-dms-deliv@izivia.com")
                    url.set("https://www.izivia.com/")
                    organization.set("IZIVIA")
                    organizationUrl.set("https://www.izivia.com/")
                }
            }
        }
    }
}

extensions.getByType<SigningExtension>()
    .sign(extensions.getByType<PublishingExtension>().publications.named("maven").get())
if (System.getenv("GPG_PRIVATE_KEY") != null) {
    extensions.getByType<SigningExtension>().useInMemoryPgpKeys(
        Base64.getDecoder().decode(System.getenv("GPG_PRIVATE_KEY")).decodeToString(),
        System.getenv("GPG_PASSPHRASE")
    )
}

tasks.withType<Sign> {
    onlyIf { System.getenv("GPG_PRIVATE_KEY") != null }
}
