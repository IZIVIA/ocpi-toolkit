package com.izivia.ocpi.toolkit.tests

import kotlinx.coroutines.runBlocking

fun runTest(fn: suspend () -> Unit) {
    runBlocking {
        fn()
    }
}
