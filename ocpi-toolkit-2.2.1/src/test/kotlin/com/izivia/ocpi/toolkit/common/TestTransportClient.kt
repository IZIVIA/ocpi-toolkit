package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import kotlinx.coroutines.runBlocking

class TestTransportClient(private var client: TransportClient) {
    fun send(request: HttpRequest): HttpResponse = runBlocking {
        client.send(request)
    }
}
