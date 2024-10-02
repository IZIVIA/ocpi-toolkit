package com.izivia.ocpi.toolkit.modules.credentials.domain

/**
 * Every role needs a unique combination of: role, party_id and country_code.
 *
 * A partner can have the same role more than once, each with its own unique party_id and country_code, for example
 * when a CPO provides 'white-label' services for 'virtual' CPOs.
 *
 * One or more roles and thus party_id and country_code sets are provided here to inform a server about the party_id and
 * country_code sets a client will use when pushing Client Owned Objects. This helps a server to determine the URLs a
 * client will use when pushing a Client Owned Object. The country_code is added to make certain the URL used when
 * pushing a Client Owned Object is unique as there might be multiple parties in the world with the same party_id. The
 * combination of country_id and party_id should always be unique though. A party operating in multiple countries can
 * always use the home country of the company for all connections.
 *
 * For example: EVSE IDs can be pushed under the country and provider identification of a company, even if the EVSEs are
 * actually located in a different country. This way it is not necessary to establish one OCPI connection per country a
 * company operates in.
 *
 * The party_id and country_code given here have no direct link with the eMI3 EVSE IDs and Contract IDs that might be
 * used in the different OCPI modules. A party implementing OCPI MAY push EVSE IDs with an eMI3 spot operator different
 * from the OCPI party_id and/or the country_code.
 *
 * A Hub SHALL only reports itself as role: Hub. A Hub SHALL NOT report all the other connected parties as a role on the
 * partner. A Hub SHALL report connected parties via the HubClientInfo module.
 *
 * @property token Case Sensitive, ASCII only. The credentials token for the other party to authenticate in your system.
 * Not encoded in Base64 or any other encoding.
 * @property url The URL to your API versions endpoint.
 * @property roles List of the roles this party provides.
 */
data class Credentials(
    val token: String,
    val url: String,
    val roles: List<CredentialRole>,
)
