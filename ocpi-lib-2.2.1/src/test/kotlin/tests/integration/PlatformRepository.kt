package tests.mock

import com.mongodb.client.MongoCollection
import ocpi.credentials.repositories.PlatformRepository
import ocpi.versions.domain.Endpoint
import ocpi.versions.domain.Version
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import samples.common.Platform

class PlatformMongoRepository(
    private val collection: MongoCollection<Platform>
) : PlatformRepository {
    override fun saveVersion(platformUrl: String, version: Version): Version = version.also {
        collection
            .updateOne(Platform::url eq platformUrl, set(Platform::version setTo it))
    }

    override fun saveEndpoints(platformUrl: String, endpoints: List<Endpoint>): List<Endpoint> = endpoints.also {
        collection
            .updateOne(Platform::url eq platformUrl, set(Platform::endpoints setTo it))
    }

    override fun saveCredentialsTokenB(platformUrl: String, credentialsTokenB: String): String =
        credentialsTokenB.also {
            collection
                .updateOne(Platform::url eq platformUrl, set(Platform::tokenB setTo it))
        }

    override fun saveCredentialsTokenC(platformUrl: String, credentialsTokenC: String): String =
        credentialsTokenC.also {
            collection
                .updateOne(Platform::url eq platformUrl, set(Platform::tokenC setTo it))
        }

    override fun getCredentialsTokenA(platformUrl: String): String? = collection
        .findOne(Platform::url eq platformUrl)?.tokenA

    override fun getCredentialsTokenC(platformUrl: String): String? = collection
        .findOne(Platform::url eq platformUrl)?.tokenC

    override fun getPlatformByTokenA(token: String): String? = collection
        .findOne(Platform::tokenA eq token)?.url

    override fun getPlatformByTokenB(token: String): String? = collection
        .findOne(Platform::tokenB eq token)?.url

    override fun getPlatformByTokenC(token: String): String? = collection
        .findOne(Platform::tokenC eq token)?.url

    override fun removeCredentialsTokenA(platformUrl: String) {
        collection.updateOne(Platform::url eq platformUrl, set(Platform::tokenA setTo null))
    }

    override fun removeCredentialsTokenC(platformUrl: String) {
        collection.updateOne(Platform::url eq platformUrl, set(Platform::tokenC setTo null))
    }

    override fun removeVersion(platformUrl: String) {
        collection.updateOne(Platform::url eq platformUrl, set(Platform::version setTo null))
    }

    override fun removeEndpoints(platformUrl: String) {
        collection.updateOne(Platform::url eq platformUrl, set(Platform::endpoints setTo null))
    }
}