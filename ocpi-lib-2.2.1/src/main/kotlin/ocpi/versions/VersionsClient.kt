package ocpi.versions

import common.OcpiResponseBody
import common.parseBody
import ocpi.versions.domain.Version
import ocpi.versions.domain.VersionDetails
import transport.TransportClient
import transport.domain.HttpMethod
import transport.domain.HttpRequest

class VersionsClient(
    private val transportClient: TransportClient
): VersionsInterface {

    override fun getVersions(token: String): OcpiResponseBody<List<Version>> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/",
                    headers = mapOf(
                        "Authorization" to "Token $token"
                    )
                )
            )
            .parseBody()

    override fun getVersionDetails(token: String, versionNumber: String): OcpiResponseBody<VersionDetails> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$versionNumber",
                    headers = mapOf(
                        "Authorization" to "Token $token"
                    )
                )
            )
            .parseBody()
}