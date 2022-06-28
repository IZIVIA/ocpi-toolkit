package com.izivia.ocpi.toolkit.processor

import de.jensklingenberg.mpapt.common.MpAptProject
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.js.translate.extensions.JsSyntheticTranslateExtension

open class CommonComponentRegistrar : ComponentRegistrar {

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val processor = PartialAnnotationProcessor()
        val apt = MpAptProject(processor, configuration)
        StorageComponentContainerContributor.registerExtension(project, apt)
        ClassBuilderInterceptorExtension.registerExtension(project, apt)
        JsSyntheticTranslateExtension.registerExtension(project, apt)
    }
}
