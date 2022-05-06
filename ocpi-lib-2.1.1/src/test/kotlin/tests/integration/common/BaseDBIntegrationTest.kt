package tests.integration.common

import org.litote.kmongo.KMongo
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
abstract class BaseDBIntegrationTest {

    @Container
    protected val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:5.0.8"))

    protected fun buildDBClient() =
        KMongo.createClient(mongoDBContainer.replicaSetUrl)
}