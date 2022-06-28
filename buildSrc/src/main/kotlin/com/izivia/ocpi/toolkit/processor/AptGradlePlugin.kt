package com.izivia.ocpi.toolkit.processor

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class AptGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> =
        kotlinCompilation.target.project.provider {
            emptyList<SubpluginOption>()
        }

    override fun getCompilerPluginId(): String = "AptGradlePlugin"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = "com.izivia",
            artifactId = "ocpi-annotation-processor",
            version = "1.0.0"
        )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

}
