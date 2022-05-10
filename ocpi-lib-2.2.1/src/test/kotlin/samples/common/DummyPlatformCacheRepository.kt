package samples.common

class DummyPlatformCacheRepository(private val tokenC: String): PlatformCacheRepository() {

    override fun getPlatformByTokenC(token: String): String? = super.getPlatformByTokenC(token)
        ?: (if (token == tokenC) "*" else null)

    override fun getCredentialsTokenC(platformUrl: String): String = super.getCredentialsTokenC(platformUrl)
        ?: tokenC
}