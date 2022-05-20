package com.izivia.ocpi.toolkit.transport.domain

class HttpException(val status: HttpStatus, val reason: String) :
    Exception("HTTP Error, status $status, message: $reason")