package transport.domain

class HttpException(val code: Int) : Exception("HTTP Error, status $code")