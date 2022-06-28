package com.izivia.ocpi.toolkit.processor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import de.jensklingenberg.mpapt.common.canonicalFilePath
import de.jensklingenberg.mpapt.model.*
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.containingPackage
import org.jetbrains.kotlin.platform.TargetPlatform
import java.io.File

class PartialAnnotationProcessor : AbstractProcessor() {

    private val annotationFqdn = "com.izivia.ocpi.toolkit.annotations.Partial"
    private val partialClasses: MutableList<ClassDescriptor> = mutableListOf()

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(annotationFqdn)

    override fun isTargetPlatformSupported(platform: TargetPlatform): Boolean = true

    override fun process(roundEnvironment: RoundEnvironment) {
        partialClasses.addAll(
            roundEnvironment.getElementsAnnotatedWith(annotationFqdn)
                .filterIsInstance<Element.ClassElement>()
                .filter { it.classDescriptor.isData }
                .map { it.classDescriptor }
        )
    }

    override fun processingOver() {
        partialClasses.forEach { classDescriptor ->
            val packageName = classDescriptor.containingPackage().toString()
            val className = classDescriptor.name.asString()
            val classFilePath = classDescriptor.canonicalFilePath().toString()
            val classFile = File(classFilePath)

            val partialClassName = className.toPartial()
            val partialClassType = buildPartialDataClassType(classDescriptor, partialClassName)
            val partialBuilderFun = buildPartialDataClassBuilderFunction(classDescriptor)
            val partialListBuilderFun = buildPartialDataClassListBuilderFunction(classDescriptor)
            val partialGenFolder = classFile.parent
                .replace("/src/main/kotlin", "/src/main/kotlinGen")
                .replace(packageName.replace(".", "/"), "")

            FileSpec
                .builder(packageName, partialClassName)
                .generateComments()
                .addType(partialClassType)
                .addFunction(partialBuilderFun)
                .addFunction(partialListBuilderFun)
                .build()
                .writeTo(File(partialGenFolder))
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
            .addKdoc(
                "Partial representation of [${
                    classDescriptor.containingPackage().toString()
                }.${classDescriptor.name.asString()}]"
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameters(partialClassConstructorParameters)
                    .build()
            )
            .addProperties(partialClassProperties)
            .build()
    }

    private fun buildPartialDataClassBuilderFunction(classDescriptor: ClassDescriptor): FunSpec {
        val packageName = classDescriptor.containingPackage().toString()
        val className = classDescriptor.name.asString()
        val baseClassConstructorParameters = classDescriptor.constructors.first()
            .getFunctionParameters()
        val partialClassConstructorParameters = baseClassConstructorParameters
            .joinToString(separator = ",\n  ") { param ->
                "${param.parameterName} = ${
                    findPartialClassByName(param.type.asString()!!.toBase())
                        ?.let { "${param.parameterName}${if (param.nullable) "?" else ""}.toPartial()" } ?: param.parameterName
                }"
            }
        return FunSpec.builder("toPartial")
            .receiver(ClassName(packageName, className))
            .returns(ClassName(packageName, className.toPartial()))
            .addCode(
                """
                | return ${className.toPartial()}(
                |   $partialClassConstructorParameters
                | )
                """.trimMargin()
            )
            .build()
    }

    private fun buildPartialDataClassListBuilderFunction(classDescriptor: ClassDescriptor): FunSpec {
        val packageName = classDescriptor.containingPackage().toString()
        val className = classDescriptor.name.asString()
        return FunSpec.builder("toPartial")
            .receiver(
                ClassName("kotlin.collections", "List")
                    .parameterizedBy(ClassName(packageName, className))
            )
            .returns(
                ClassName("kotlin.collections", "List")
                    .parameterizedBy(ClassName(packageName, className.toPartial()))
            )
            .addCode(
                """
                | return mapNotNull { it.toPartial() }
                """.trimMargin()
            )
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
                        .wireWithExistingPartial()
                    val packageName = fullPackage.substringBefore("<").split(".")
                        .dropLast(1).joinToString(".")
                    val parameterType = fullPackage.substringAfter("<").substringBefore(">")
                        .split(".").last().replace("?", "")
                        .wireWithExistingPartial()
                    val parameterTypePackage = fullPackage.substringAfter("<").substringBefore(">")
                        .split(".").dropLast(1).joinToString(".")
                    ClassName(
                        packageName,
                        type
                    )
                        .parameterizedBy(ClassName(parameterTypePackage, parameterType))
                } else {
                    val packageName = fullPackage.split(".").dropLast(1).joinToString(".")
                    val typeName = fullPackage.split(".").last().replace("?", "")
                        .wireWithExistingPartial()
                    ClassName(
                        packageName,
                        typeName
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

    private fun String.wireWithExistingPartial(): String =
        findPartialClassByName(this)
            ?.toPartial()
            ?: this

    private fun findPartialClassByName(name: String) =
        partialClasses
            .map { it.name.asString() }
            .find { it == name }

    private fun TypeName.asString(): String? = when (this) {
        is ClassName -> simpleName
        is ParameterizedTypeName -> this.typeArguments.first().asString()
        else -> null
    }

    private fun String.toPartial() = "${this}Partial"
    private fun String.toBase() = replace("Partial", "")

    private fun FileSpec.Builder.generateComments() =
        addFileComment(
            """
            | ----------
            | - WARNING -
            | ----------
            | This code is generated AND MUST NOT BE EDITED
            | ----------
            """.trimMargin()
        )

    data class FunctionParameter(
        val parameterName: String,
        val nullable: Boolean,
        val type: TypeName
    )
}
