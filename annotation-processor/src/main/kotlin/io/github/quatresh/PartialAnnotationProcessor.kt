package io.github.quatresh

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import de.jensklingenberg.mpapt.common.canonicalFilePath
import de.jensklingenberg.mpapt.model.*
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.platform.TargetPlatform
import java.io.File

class PartialAnnotationProcessor : AbstractProcessor() {

    private val annotationFqdn = "io.github.quatresh.annotations.Partial"
    private var partialClasses: List<ClassDescriptor> = emptyList()

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(annotationFqdn)

    override fun isTargetPlatformSupported(platform: TargetPlatform): Boolean = true

    override fun process(roundEnvironment: RoundEnvironment) {
        partialClasses = roundEnvironment.getElementsAnnotatedWith(annotationFqdn)
            .filterIsInstance<Element.ClassElement>()
            .filter { it.classDescriptor.isData }
            .map { it.classDescriptor }
    }

    override fun processingOver() {
        partialClasses.forEach { classDescriptor ->
            val classFilePath = classDescriptor.canonicalFilePath().toString()
            val className = classDescriptor.name.asString()
            val classFile = File(classFilePath)
            val partialClassName = "${className}Partial"
            if (!classFile.readText().contains("data class $partialClassName")) {
                buildPartialDataClassType(classDescriptor, partialClassName)
                    .toString()
                    .replace("public", "")
                    .also {
                        classFile.appendText("\n$it")
                    }
            }
        }
    }

    private fun buildPartialDataClassType(classDescriptor: ClassDescriptor, partialClassName: String): TypeSpec {
        val baseClassConstructorParameters = classDescriptor.constructors.first()
            .getFunctionParameters()
        val partialClassConstructorParameters = baseClassConstructorParameters
            .map { param ->
                ParameterSpec
                    .builder(
                        param.parameterName,
                        param.type.copy(nullable = true)
                    )
                    .build()
            }

        val partialClassProperties = baseClassConstructorParameters
            .map { param ->
                PropertySpec
                    .builder(
                        param.parameterName, param.type.copy(nullable = true)
                    )
                    .initializer(param.parameterName)
                    .build()
            }

        return TypeSpec
            .classBuilder(partialClassName)
            .addModifiers(KModifier.DATA)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameters(partialClassConstructorParameters)
                    .build()
            )
            .addProperties(partialClassProperties)
            .build()
    }

    private fun FunctionDescriptor.getFunctionParameters(): List<FunctionParameter> {
        return if (valueParameters.isNotEmpty()) {
            this.valueParameters.map { parameter ->
                val fullPackage = parameter.toString()
                    .substringAfter(": ")
                    .substringBefore(" defined")
                    .substringBefore(" /* =")
                    .substringBefore("=")
                    .trim()

                val typeName = if (fullPackage.contains("<")) {
                    val type = fullPackage.substringBefore("<")
                        .split(".").last().replace("?", "")
                    val packageName = fullPackage.substringBefore("<").split(".")
                        .dropLast(1).joinToString(".")
                    val parameterType = fullPackage.substringAfter("<").substringBefore(">")
                        .split(".").last().replace("?", "")
                    val parameterTypePackage = fullPackage.substringAfter("<").substringBefore(">")
                        .split(".").dropLast(1).joinToString(".")
                    ClassName(
                        packageName,
                        type
                    )
                        .parameterizedBy(ClassName(parameterTypePackage, parameterType))
                } else {
                    ClassName(
                        fullPackage.split(".").dropLast(1).joinToString("."),
                        fullPackage.split(".").last().replace("?", "")
                    )
                }
                FunctionParameter(
                    parameter.name.asString(),
                    parameter.type.toString().endsWith("?"),
                    typeName
                )
            }.toList()
        } else {
            emptyList()
        }
    }

    data class FunctionParameter(
        val parameterName: String,
        val nullable: Boolean,
        val type: TypeName
    )
}
