package common

import com.fasterxml.jackson.module.kotlin.readValue
import transport.domain.HttpResponse
import java.util.*

inline fun <reified T> HttpResponse.parseBody(): T = mapper.readValue(body!!)

fun String.encodeBase64(): String = Base64.getEncoder().encodeToString(this.encodeToByteArray())
fun String.decodeBase64(): String = Base64.getDecoder().decode(this).decodeToString()