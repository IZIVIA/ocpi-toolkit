package com.izivia.ocpi.toolkit.common

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class PathOfTest {
    @Test
    fun `each segment is encoded on its own`() {
        expectThat(pathOf("FR", "LAB", "04 AB/1%", "authorize")).isEqualTo("/FR/LAB/04%20AB%2F1%25/authorize")
    }

    @Test
    fun `an EVSE uid keeps its stars`() {
        expectThat(pathOf("FR*LAB*E1")).isEqualTo("/FR*LAB*E1")
    }
}
