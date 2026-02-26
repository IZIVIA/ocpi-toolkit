package com.izivia.ocpi.toolkit.modules.commands

import com.izivia.ocpi.toolkit.common.TransportClientBuilder
import com.izivia.ocpi.toolkit.common.parseResultOrNull
import com.izivia.ocpi.toolkit.modules.commands.domain.CommandResult
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

class CommandCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
) {
    suspend fun postCommandCallback(
        commandResult: CommandResult,
        partnerId: String,
        responseUrl: String,
    ) =
        transportClientBuilder
            .buildFor(partnerId, responseUrl)
            .send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "",
                    body = mapper.serializeObject(commandResult),
                ),
            ).parseResultOrNull<String>()
}
