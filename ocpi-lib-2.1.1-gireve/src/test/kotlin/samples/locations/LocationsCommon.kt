package samples.locations

import samples.common.DummyPlatformCacheRepository
import java.util.*

val CREDENTIALS_TOKEN_C = UUID.randomUUID().toString()
val DUMMY_CREDENTIALS_REPOSITORY = DummyPlatformCacheRepository(token = CREDENTIALS_TOKEN_C)