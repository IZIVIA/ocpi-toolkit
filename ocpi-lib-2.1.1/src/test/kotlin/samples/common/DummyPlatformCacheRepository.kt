package samples.common

class DummyPlatformCacheRepository(token: String): PlatformCacheRepository() {

    init {
        platforms["*"] = Platform("*", tokenC = token)
    }
}