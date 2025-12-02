package com.izivia.ocpi.toolkit.modules.tariff

import com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit.modules.tariff.domain.TariffPartial

/**
 * Typically implemented by market roles like: eMSP and NSP.
 * Tariffs are Client Owned Objects, so the endpoints need to contain the required extra fields: {party_id} and
 * {country_code}. Endpoint structure definition:
 * - {tariffs_endpoint_url}/{country_code}/{party_id}/{tariff_id}
 *
 * Method: Description
 * - GET: Retrieve a Tariff as it is stored in the eMSP’s system.
 * - POST: n/a
 * - PUT: Push new/updated Tariff object to the eMSP.
 * - PATCH: n/a
 * - DELETE: Remove a Tariff object which is no longer in use and will not be used in future either.
 */
interface TariffEmspInterface {

    /**
     * If the CPO wants to check the status of a Tariff in the eMSP’s system, it might GET the object from the eMSP’s
     * system for validation purposes. After all, the CPO is the owner of the object, so it would be illogical if the
     * eMSP’s system had a different status or was missing the object entirely.
     *
     * @param countryCode (max-length=2) Country code of the CPO performing the GET request on the eMSP’s system.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO performing the GET request on the eMSP’s system.
     * @param tariffId (max-length=36) Tariff.id of the Tariff object to retrieve.
     */
    suspend fun getTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
    ): Tariff?

    /**
     * New or updated Tariff objects are pushed from the CPO to the eMSP.
     *
     * @param countryCode (max-length=2) Country code of the CPO performing the PUT request on the eMSP’s system. This
     * SHALL be the same value as the country_code in the Tariff object being pushed.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO performing the PUT request on the eMSP’s system.
     * This SHALL be the same value as the party_id in the Tariff object being pushed.
     * @param tariff (max-length=36) Tariff.id of the Tariff object to create or replace.
     */
    suspend fun putTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
        tariff: Tariff,
    ): TariffPartial

    /**
     * Delete a Tariff object which is not used any more and will not be used in the future.
     *
     * @param countryCode (max-length=2) Country code of the CPO performing the PUT request on the eMSP’s system.
     * @param partyId (max-length=3) Party ID (Provider ID) of the CPO performing the PUT request on the eMSP’s system.
     * @param tariff (max-length=36) Tariff.id of the Tariff object to delete.
     */
    suspend fun deleteTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
    )
}
