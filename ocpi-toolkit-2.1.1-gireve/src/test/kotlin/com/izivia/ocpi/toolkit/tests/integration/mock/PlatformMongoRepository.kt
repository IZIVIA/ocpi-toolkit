package com.izivia.ocpi.toolkit.tests.integration.mock

import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.samples.common.Platform
import com.mongodb.client.MongoCollection
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.set
import org.litote.kmongo.setTo

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

    override fun getEndpoints(platformUrl: String): List<Endpoint> = collection
        .findOne(Platform::url eq platformUrl)?.endpoints ?: emptyList()

    override fun getVersion(platformUrl: String): Version? = collection
        .findOne(Platform::url eq platformUrl)?.version


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