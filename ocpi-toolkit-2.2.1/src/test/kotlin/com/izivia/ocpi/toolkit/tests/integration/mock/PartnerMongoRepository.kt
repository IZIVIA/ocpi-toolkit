package com.izivia.ocpi.toolkit.tests.integration.mock

import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.samples.common.Partner
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

class PartnerMongoRepository(
    private val collection: MongoCollection<Partner>,
) : PartnerRepository {

    override suspend fun savePartnerUrlForTokenA(tokenA: String, partnerUrl: String): String? =
        collection
            .findOneAndUpdate(
                Partner::tokenA eq tokenA,
                set(Partner::url setTo partnerUrl),
                FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER),
            )
            ?.url

    override suspend fun saveCredentialsRoles(
        partnerId: String,
        credentialsRoles: List<CredentialRole>,
    ): List<CredentialRole> =
        credentialsRoles.also {
            collection
                .updateOne(Partner::url eq partnerId, set(Partner::credentialsRoles setTo it))
        }

    override suspend fun saveVersion(partnerId: String, version: Version): Version = version.also {
        collection
            .updateOne(Partner::url eq partnerId, set(Partner::version setTo it))
    }

    override suspend fun saveEndpoints(partnerId: String, endpoints: List<Endpoint>): List<Endpoint> = endpoints.also {
        collection
            .updateOne(Partner::url eq partnerId, set(Partner::endpoints setTo it))
    }

    override suspend fun saveCredentialsClientToken(partnerId: String, credentialsClientToken: String): String =
        credentialsClientToken.also {
            collection
                .updateOne(Partner::url eq partnerId, set(Partner::clientToken setTo it))
        }

    override suspend fun saveCredentialsServerToken(partnerId: String, credentialsServerToken: String): String =
        credentialsServerToken.also {
            collection
                .updateOne(Partner::url eq partnerId, set(Partner::serverToken setTo it))
        }

    override suspend fun getCredentialsTokenA(partnerId: String): String? = collection
        .findOne(Partner::url eq partnerId)?.tokenA

    override suspend fun getCredentialsClientToken(partnerId: String): String? = collection
        .findOne(Partner::url eq partnerId)?.clientToken

    override suspend fun isCredentialsTokenAValid(credentialsTokenA: String): Boolean = collection
        .findOne(Partner::tokenA eq credentialsTokenA) != null

    override suspend fun isCredentialsServerTokenValid(credentialsServerToken: String): Boolean = collection
        .findOne(Partner::serverToken eq credentialsServerToken) != null

    override suspend fun getPartnerUrl(partnerId: String): String? = collection
        .findOne(Partner::url eq partnerId)?.url

    override suspend fun getPartnerUrlByCredentialsServerToken(credentialsServerToken: String): String? = collection
        .findOne(Partner::serverToken eq credentialsServerToken)?.url

    override suspend fun getPartnerIdByCredentialsServerToken(credentialsServerToken: String): String? = collection
        .findOne(Partner::serverToken eq credentialsServerToken)?.url

    override suspend fun getPartnerIdByCredentialsTokenA(credentialsTokenA: String): String? = collection.findOne(
        Partner::tokenA eq credentialsTokenA,
    )?.url

    override suspend fun getEndpoints(partnerId: String): List<Endpoint> = collection
        .findOne(Partner::url eq partnerId)?.endpoints ?: emptyList()

    override suspend fun getVersion(partnerId: String): Version? = collection
        .findOne(Partner::url eq partnerId)?.version

    override suspend fun invalidateCredentialsTokenA(partnerId: String): Boolean =
        collection
            .updateOne(Partner::url eq partnerId, set(Partner::tokenA setTo null))
            .matchedCount == 1L

    override suspend fun invalidateCredentialsClientToken(partnerId: String): Boolean =
        collection
            .updateOne(
                Partner::url eq partnerId,
                combine(
                    set(Partner::clientToken setTo null),
                ),
            )
            .matchedCount == 1L

    override suspend fun invalidateCredentialsServerToken(partnerId: String): Boolean =
        collection
            .updateOne(
                Partner::url eq partnerId,
                combine(
                    set(Partner::serverToken setTo null),
                ),
            )
            .matchedCount == 1L
}
