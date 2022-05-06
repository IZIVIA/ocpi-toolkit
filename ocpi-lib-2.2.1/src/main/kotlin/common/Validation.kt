package common

import ocpi.credentials.repositories.PlatformRepository

/**
 * TODO: is it the good behaviour given:
 * - tokenA: Valid in receiver context, during sender registration (only for sender -> receiver calls)
 * - tokenB: Valid in sender context, during sender registration (only for receiver -> sender calls)
 * - tokenC: Valid when the sender is registered with the receiver (only for sender -> receiver)
 *
 * @throws OcpiClientInvalidParametersException if the token is invalid, otherwise does nothing
 */
fun validateToken(platformRepository: PlatformRepository, token: String) {
    if (platformRepository.getPlatformByTokenA(token) == null &&
        platformRepository.getPlatformByTokenB(token) == null &&
        platformRepository.getPlatformByTokenC(token) == null) {

        throw OcpiClientInvalidParametersException("Invalid token: $token")
    }
}
