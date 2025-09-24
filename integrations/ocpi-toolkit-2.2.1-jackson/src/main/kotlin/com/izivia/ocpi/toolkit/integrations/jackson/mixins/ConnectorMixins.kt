package com.izivia.ocpi.toolkit.integrations.jackson.mixins

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.izivia.ocpi.toolkit.common.CiString

class ConnectorMixin {
    @JsonSetter(contentNulls = Nulls.SKIP)
    val tariffIds: List<CiString>? = null
}
