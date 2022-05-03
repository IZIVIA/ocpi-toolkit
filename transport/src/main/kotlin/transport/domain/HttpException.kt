package transport.domain

class HttpException(val status: HttpStatus) : Exception("HTTP Error, status $status")