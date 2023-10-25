package com.izivia.ocpi.toolkit.tests.integration.mock

import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.samples.common.Platform
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.set
import org.litote.kmongo.setTo

class PlatformMongoRepository(
    private val collection: MongoCollection<Platform>
) : PlatformRepository {

    override suspend fun savePlatformUrlForTokenA(tokenA: String, platformUrl: String): String? =
        collection
            .findOneAndUpdate(
                Platform::tokenA eq tokenA,
                set(Platform::url setTo platformUrl),
                FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
            )
            ?.url

    override suspend fun saveVersion(platformUrl: String, version: Version): Version = version.also {
        collection
            .updateOne(Platform::url eq platformUrl, set(Platform::version setTo it))
    }

    override suspend fun saveEndpoints(platformUrl: String, endpoints: List<Endpoint>): List<Endpoint> = endpoints.also {
        collection
            .updateOne(Platform::url eq platformUrl, set(Platform::endpoints setTo it))
    }

    override suspend fun saveCredentialsTokenA(platformUrl: String, credentialsTokenA: String): String =
        credentialsTokenA.also {
            collection
                .updateOne(Platform::url eq platformUrl, set(Platform::tokenA setTo it))
        }

    override suspend fun saveCredentialsTokenB(platformUrl: String, credentialsTokenB: String): String =
        credentialsTokenB.also {
            collection
                .updateOne(Platform::url eq platformUrl, set(Platform::tokenB setTo it))
        }

    override suspend fun saveCredentialsTokenC(platformUrl: String, credentialsTokenC: String): String =
        credentialsTokenC.also {
            collection
                .updateOne(Platform::url eq platformUrl, set(Platform::tokenC setTo it))
        }

    override suspend fun getCredentialsTokenA(platformUrl: String): String? = collection
        .findOne(Platform::url eq platformUrl)?.tokenA

    override suspend fun getCredentialsTokenB(platformUrl: String): String? = collection
        .findOne(Platform::url eq platformUrl)?.tokenB

    override suspend fun getCredentialsTokenC(platformUrl: String): String? = collection
        .findOne(Platform::url eq platformUrl)?.tokenC

    override suspend fun platformExistsWithTokenA(token: String): Boolean = collection
        .findOne(Platform::tokenA eq token) != null

    override suspend fun platformExistsWithTokenB(token: String): Boolean = collection
        .findOne(Platform::tokenB eq token) != null

    override suspend fun getPlatformByTokenC(token: String): String? = collection
        .findOne(Platform::tokenC eq token)?.url

    override suspend fun getEndpoints(platformUrl: String): List<Endpoint> = collection
        .findOne(Platform::url eq platformUrl)?.endpoints ?: emptyList()

    override suspend fun getVersion(platformUrl: String): Version? = collection
        .findOne(Platform::url eq platformUrl)?.version

    override suspend fun removeCredentialsTokenA(platformUrl: String) {
        collection.updateOne(Platform::url eq platformUrl, set(Platform::tokenA setTo null))
    }

    override suspend fun removeCredentialsTokenB(platformUrl: String) {
        collection.updateOne(Platform::url eq platformUrl, set(Platform::tokenB setTo null))
    }

    override suspend fun removeCredentialsTokenC(platformUrl: String) {
        collection.updateOne(Platform::url eq platformUrl, set(Platform::tokenC setTo null))
    }

    override suspend fun removeVersion(platformUrl: String) {
        collection.updateOne(Platform::url eq platformUrl, set(Platform::version setTo null))
    }

    override suspend fun removeEndpoints(platformUrl: String) {
        collection.updateOne(Platform::url eq platformUrl, set(Platform::endpoints setTo null))
    }
}
