package com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.generated

import com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.ocpiSerializersModule

// Introduced to prevent backward incompatibilities after introducing konstruct lib. Before introducing this lib, the
// generated serializers were generated and available in the package
// "com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.generated"
// under the field "generatedSerializersModule". Now it's in
// "com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.ocpiSerializersModule"
// under the field "ocpiSerializersModule," which is more explicit for users of the lib (that may have several
// serializers modules)
@Deprecated(
    message = "Use ocpiSerializersModule instead",
    replaceWith = ReplaceWith(
        "ocpiSerializersModule",
        "com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.ocpiSerializersModule"
    ),
    level = DeprecationLevel.WARNING
)
val generatedSerializersModule = ocpiSerializersModule
