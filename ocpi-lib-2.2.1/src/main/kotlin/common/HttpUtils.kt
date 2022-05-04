package common

import com.fasterxml.jackson.module.kotlin.readValue
import transport.domain.HttpException
import transport.domain.HttpResponse
import transport.domain.HttpStatus

inline fun <reified T> HttpResponse.parseBody(): T =
    if (status == HttpStatus.OK || status == HttpStatus.CREATED) {
        mapper.readValue(body!!)
    } else {
        throw HttpException(status, status.label)
    }