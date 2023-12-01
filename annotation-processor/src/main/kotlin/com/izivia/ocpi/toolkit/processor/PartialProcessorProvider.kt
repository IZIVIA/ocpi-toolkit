package com.izivia.ocpi.toolkit.processor

import com.google.devtools.ksp.processing.*

class PartialProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        PartialProcessor(environment.codeGenerator, environment.logger)
}
