package ocpi.credentials.domain

import java.util.*

data class CiString(val str: String) {
    override fun toString(): String {
        return str.lowercase(Locale.getDefault())
    }
}
