package ocpi.credentials.domain

import ocpi.locations.domain.BusinessDetails

/**
 * The party_id and country_code are provided here to inform a server about the party_id and country_code a client will
 * use when pushing client owned objects. This helps a server determine the URLs a client will use when pushing a client
 * owned object. The country_code is added the make certain the URL used when pushing a client owned object is unique,
 * there might be multiple parties in the world with the same party_id, but the combination should always be unique. A
 * party operating in multiple countries can always use the home country of the company for all connections. For
 * example: an OCPI implementation might push EVSE IDs from a company for different countries, preventing an OCPI
 * connection per country a company is operating in. The party_id and country_code give here, have no direct link with
 * the eMI3 EVSE IDs and Contract IDs that might be used in the different OCPI modules. For example: an implementation
 * OCPI might push EVSE IDs with an eMI3 spot operator different from the OCPI party_id and/or the country_code.
 *
 * @property token (max-length=64) Case Sensitive, ASCII only. The credentials token for the other party to authenticate
 * in your system. Not encoded in Base64 or any other encoding.
 * @property url The URL to your API versions endpoint.
 * @property business_details Details of this party.
 * @property party_id (max-length=3) CPO or eMSP ID of this party. (following the 15118 ISO standard).
 * @property country_code(max-length=2)  Country code of the country this party is operating in.
 */
data class Credentials(
    val token: String,
    val url: String,
    val business_details: BusinessDetails,
    val party_id: String,
    val country_code: String
)
