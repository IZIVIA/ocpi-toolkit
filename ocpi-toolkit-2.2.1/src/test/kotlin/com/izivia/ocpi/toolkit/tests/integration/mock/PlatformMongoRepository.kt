package com.izivia.ocpi.toolkit.tests.integration.mock

import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.samples.common.Platform
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

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

    override suspend fun saveCredentialsRoles(
        platformUrl: String,
        credentialsRoles: List<CredentialRole>
    ): List<CredentialRole> =
        credentialsRoles.also {
            collection
                .updateOne(Platform::url eq platformUrl, set(Platform::credentialsRoles setTo it))
        }

    override suspend fun saveVersion(platformUrl: String, version: Version): Version = version.also {
        collection
            .updateOne(Platform::url eq platformUrl, set(Platform::version setTo it))
    }

    override suspend fun saveEndpoints(platformUrl: String, endpoints: List<Endpoint>): List<Endpoint> = endpoints.also {
        collection
            .updateOne(Platform::url eq platformUrl, set(Platform::endpoints setTo it))
    }

    override suspend fun saveCredentialsClientToken(platformUrl: String, credentialsClientToken: String): String =
        credentialsClientToken.also {
            collection
                .updateOne(Platform::url eq platformUrl, set(Platform::clientToken setTo it))
        }

    override suspend fun saveCredentialsServerToken(platformUrl: String, credentialsServerToken: String): String =
        credentialsServerToken.also {
            collection
                .updateOne(Platform::url eq platformUrl, set(Platform::serverToken setTo it))
        }

    override suspend fun getCredentialsTokenA(platformUrl: String): String? = collection
        .findOne(Platform::url eq platformUrl)?.tokenA

    override suspend fun getCredentialsClientToken(platformUrl: String): String? = collection
        .findOne(Platform::url eq platformUrl)?.clientToken

    override suspend fun isCredentialsTokenAValid(credentialsTokenA: String): Boolean = collection
        .findOne(Platform::tokenA eq credentialsTokenA) != null

    override suspend fun isCredentialsServerTokenValid(credentialsServerToken: String): Boolean = collection
        .findOne(Platform::serverToken eq credentialsServerToken) != null

    override suspend fun getPlatformUrlByCredentialsServerToken(credentialsServerToken: String): String? = collection
        .findOne(Platform::serverToken eq credentialsServerToken)?.url

    override suspend fun getEndpoints(platformUrl: String): List<Endpoint> = collection
        .findOne(Platform::url eq platformUrl)?.endpoints ?: emptyList()

    override suspend fun getVersion(platformUrl: String): Version? = collection
        .findOne(Platform::url eq platformUrl)?.version

    override suspend fun invalidateCredentialsTokenA(platformUrl: String): Boolean =
        collection
            .updateOne(Platform::url eq platformUrl, set(Platform::tokenA setTo null))
            .matchedCount == 1L

    override suspend fun unregisterPlatform(platformUrl: String): Boolean =
        collection
            .updateOne(
                Platform::url eq platformUrl,
                combine(
                    set(Platform::tokenA setTo null),
                    set(Platform::clientToken setTo null),
                    set(Platform::serverToken setTo null),
                    set(Platform::version setTo null),
                    set(Platform::endpoints setTo null)
                )
            )
            .matchedCount == 1L
}
