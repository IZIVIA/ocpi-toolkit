package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.commands.domain.CommandResult
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

class CommandCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerRepository: PartnerRepository
) {
    suspend fun postCommandCallback(
        commandResult: CommandResult,
        partnerUrl: String,
        responseUrl: String
    ): OcpiResponseBody<Any> =
        transportClientBuilder
            .build("")
            .send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = responseUrl,
                    body = mapper.writeValueAsString(commandResult)
                )
                    .withRequiredHeaders(
                        requestId = generateUUIDv4Token(),
                        correlationId = generateUUIDv4Token()
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerUrl = partnerUrl)
            ).parseBody()
}
