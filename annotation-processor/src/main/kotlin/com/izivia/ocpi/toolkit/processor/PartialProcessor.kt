package com.izivia.ocpi.toolkit.processor

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.validate
import com.izivia.ocpi.toolkit.annotations.Partial
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.writeTo

class PartialProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private val toPartialMethodName = "toPartial"
    private val toDomainMethodName = "toOcpiDomain"
    private val partialClasses: MutableList<KSClassDeclaration> = mutableListOf()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // partialClasses.isEmpty() is a hack to make sure process is only called once.
        // According to the documentation, ksp supports multiple rounds. And if we want
        // multiple rounds, we need to return Symbols that need to be processed in the
        // next round. Here as you can see, we return an emptyList, but ksp still calls
        // process two times. When called on the second time, it crashes because the files
        // are already created. I do not understand how it works, but this workaround does
        // the job for now.
        if (partialClasses.isEmpty()) {
            partialClasses.addAll(resolver.getPartialAnnotatedClasses())
            genPartialClasses(partialClasses.toSet())
                .forEach { (input, output) ->
                    output.writeTo(
                        codeGenerator,
                        Dependencies(true, input.containingFile!!),
                    )
                }
        }

        return emptyList()
    }

    private fun Resolver.getPartialAnnotatedClasses(): Set<KSClassDeclaration> =
        getSymbolsWithAnnotation(Partial::class.qualifiedName.orEmpty())
            .filterIsInstance<KSClassDeclaration>()
            .filter(KSNode::validate)
            .toSet()

    private fun genPartialClasses(
        partialAnnotatedClasses: Set<KSClassDeclaration>,
    ): List<Pair<KSClassDeclaration, FileSpec>> =
        partialAnnotatedClasses.map { classDeclaration ->
            val packageName = classDeclaration.packageName.asString()
            val className = classDeclaration.simpleName.asString()

            val partialClassName = className.toPartial()
            val partialClassType = buildPartialDataClassType(classDeclaration, partialClassName)
            val partialBuilderFun = buildPartialDataClassBuilderFunction(classDeclaration)
            val partialListBuilderFun = buildPartialDataClassListBuilderFunction(classDeclaration)
            val domainListBuilderFun = buildDomainFromPartialListFunction(classDeclaration)

            logger.info("Generate $packageName.$partialClassName")

            classDeclaration to FileSpec
                .builder(packageName, partialClassName)
                .generateComments()
                .addImport("com.izivia.ocpi.toolkit.common", "OcpiClientInvalidParametersException")
                .addType(partialClassType)
                .addFunction(partialBuilderFun)
                .addFunction(partialListBuilderFun)
                .addFunction(domainListBuilderFun)
                .apply {
                    getParameterTypesOutsidePackage(partialClassType, packageName)
                        .forEach { addImport(it.packageName, toPartialMethodName) }

                    // Add imports for toDomainMethodName for external partial types
                    getParameterTypesOutsidePackage(partialClassType, packageName)
                        .forEach { addImport(it.packageName, toDomainMethodName) }
                }
                .build()
        }

    private fun buildPartialDataClassType(
        classDescriptor: KSClassDeclaration,
        partialClassName: String,
    ): TypeSpec {
        val packageName = classDescriptor.packageName.asString()
        val className = classDescriptor.simpleName.asString()
        val baseClassConstructorParameters = classDescriptor
            .getConstructors()
            .first()
            .getFunctionParameters()

        val partialClassConstructorParameters = baseClassConstructorParameters
            .map { param ->
                ParameterSpec
                    .builder(
                        param.parameterName,
                        param.type.copy(nullable = true),
                    )
                    .defaultValue("null")
                    .build()
            }

        val partialClassProperties = baseClassConstructorParameters
            .map { param ->
                PropertySpec
                    .builder(
                        param.parameterName,
                        param.type.copy(nullable = true),
                    )
                    .initializer(param.parameterName)
                    .build()
            }

        val toDomainFunction = buildToDomainMemberFunction(classDescriptor)

        return TypeSpec
            .classBuilder(partialClassName)
            .addModifiers(KModifier.DATA)
            .addSuperinterface(
                ClassName("com.izivia.ocpi.toolkit.common", "Partial")
                    .parameterizedBy(ClassName(packageName, className)),
            )
            .addKdoc(
                "Partial representation of [$packageName.$className]",
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameters(partialClassConstructorParameters)
                    .build(),
            )
            .addProperties(partialClassProperties)
            .addFunction(toDomainFunction)
            .build()
    }

    private fun buildPartialDataClassBuilderFunction(classDescriptor: KSClassDeclaration): FunSpec {
        val packageName = classDescriptor.packageName.asString()
        val className = classDescriptor.simpleName.asString()
        val baseClassConstructorParameters = classDescriptor.getConstructors().first().getFunctionParameters()
        val partialClassConstructorParameters = baseClassConstructorParameters
            .joinToString(separator = ",\n    ") { param ->
                "${param.parameterName} = ${
                    findPartialClassByName(param.type.asString()!!.toBase())
                        ?.let {
                            "${param.parameterName}${if (param.nullable) "?" else ""}.$toPartialMethodName()"
                        } ?: param.parameterName
                }"
            }
        return FunSpec.builder(toPartialMethodName)
            .receiver(ClassName(packageName, className))
            .returns(ClassName(packageName, className.toPartial()))
            .addCode(
                """
                | return ${className.toPartial()}(
                |    $partialClassConstructorParameters
                | )
                """.trimMargin(),
            )
            .build()
    }

    private fun buildToDomainMemberFunction(classDescriptor: KSClassDeclaration): FunSpec {
        val packageName = classDescriptor.packageName.asString()
        val className = classDescriptor.simpleName.asString()
        val baseClassConstructorParameters = classDescriptor.getConstructors().first().getFunctionParameters()

        val funSpecBuilder = FunSpec.builder(toDomainMethodName)
            .addModifiers(KModifier.OVERRIDE)
            .returns(ClassName(packageName, className))

        // we have to use text templates, as processor would otherwise start inserting newlines in random places
        funSpecBuilder.addCode("return %T(\n", ClassName(packageName, className))

        baseClassConstructorParameters.forEach { param ->
            val paramName = param.parameterName
            val baseTypeName = param.type.asString()!!.toBase()
            val hasPartialClass = findPartialClassByName(baseTypeName) != null

            when {
                param.nullable -> {
                    if (hasPartialClass) {
                        funSpecBuilder.addCode("  %L = %L?.%L()", paramName, paramName, toDomainMethodName)
                    } else {
                        funSpecBuilder.addCode("  %L = %L", paramName, paramName)
                    }
                }

                else -> {
                    val exceptionMessage = "missing $className.$paramName"
                    if (hasPartialClass) {
                        funSpecBuilder.addCode(
                            "  %L = %L?.%L()\n    ?: throw %T(%S)",
                            paramName,
                            paramName,
                            toDomainMethodName,
                            ClassName("com.izivia.ocpi.toolkit.common", "OcpiClientInvalidParametersException"),
                            exceptionMessage,
                        )
                    } else {
                        funSpecBuilder.addCode(
                            "  %L = %L\n    ?: throw %T(%S)",
                            paramName,
                            paramName,
                            ClassName("com.izivia.ocpi.toolkit.common", "OcpiClientInvalidParametersException"),
                            exceptionMessage,
                        )
                    }
                }
            }

            funSpecBuilder.addCode(",\n")
        }

        funSpecBuilder.addCode(")")

        return funSpecBuilder.build()
    }

    private fun buildDomainFromPartialListFunction(classDescriptor: KSClassDeclaration): FunSpec {
        val packageName = classDescriptor.packageName.asString()
        val className = classDescriptor.simpleName.asString()
        val partialClassName = className.toPartial()
        return FunSpec.builder(toDomainMethodName)
            .receiver(
                ClassName("kotlin.collections", "List")
                    .parameterizedBy(ClassName(packageName, partialClassName)),
            )
            .returns(
                ClassName("kotlin.collections", "List")
                    .parameterizedBy(ClassName(packageName, className)),
            )
            .addCode(
                """
                | return map { it.$toDomainMethodName() }
                """.trimMargin(),
            )
            .build()
    }

    private fun getParameterTypesOutsidePackage(classType: TypeSpec, packageName: String): Set<ClassName> {
        val constructorSimpleTypes = classType.primaryConstructor
            ?.parameters
            ?.asSequence()
            ?.map { it.type }
            ?.filterIsInstance<ClassName>()
            .orEmpty()
        val constructorParameterizedTypes = classType.primaryConstructor
            ?.parameters
            ?.asSequence()
            ?.map { it.type }
            ?.filterIsInstance<ParameterizedTypeName>()
            ?.flatMap { it.typeArguments }
            ?.filterIsInstance<ClassName>()
            .orEmpty()
        return (constructorSimpleTypes + constructorParameterizedTypes)
            .filter { it.packageName != packageName }
            .filter { it.isPartialType() }
            .toSet()
    }

    private fun buildPartialDataClassListBuilderFunction(classDescriptor: KSClassDeclaration): FunSpec {
        val packageName = classDescriptor.packageName.asString()
        val className = classDescriptor.simpleName.asString()
        return FunSpec.builder(toPartialMethodName)
            .receiver(
                ClassName("kotlin.collections", "List")
                    .parameterizedBy(ClassName(packageName, className)),
            )
            .returns(
                ClassName("kotlin.collections", "List")
                    .parameterizedBy(ClassName(packageName, className.toPartial())),
            )
            .addCode(
                """
                | return mapNotNull { it.$toPartialMethodName() }
                """.trimMargin(),
            )
            .build()
    }

    private fun KSFunctionDeclaration.getFunctionParameters(): List<FunctionParameter> {
        return if (parameters.toList().isNotEmpty()) {
            parameters.map { parameter ->
                val resolvedType = parameter.type.resolve()
                val typeName = if (resolvedType.arguments.isNotEmpty()) {
                    // It's a type with generics, example: List<String>
                    ClassName(
                        packageName = resolvedType.declaration.packageName.asString(),
                        simpleNames = listOf(
                            resolvedType.declaration.simpleName.asString(),
                        ),
                    ).parameterizedBy(
                        resolvedType.arguments.map { generic ->
                            val genericResolved = generic.type!!.resolve()
                            ClassName(
                                packageName = genericResolved.declaration.packageName.asString(),
                                simpleNames = listOf(
                                    genericResolved.declaration.simpleName.asString().wireWithExistingPartial(),
                                ),
                            )
                        },
                    )
                } else {
                    ClassName(
                        packageName = resolvedType.declaration.packageName.asString(),
                        simpleNames = listOf(
                            resolvedType.declaration.simpleName.asString().wireWithExistingPartial(),
                        ),
                    )
                }

                FunctionParameter(
                    parameter.name?.asString() ?: throw IllegalStateException("null param name $parameter"),
                    resolvedType.isMarkedNullable,
                    typeName,
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

    private fun findPartialClassByName(name: String): String? =
        partialClasses
            .map { it.simpleName.asString() }
            .find { it == name }

    private fun String.toPartial() = "${this}Partial"

    private fun ClassName.isPartialType() = simpleName.contains("Partial")

    private fun String.toBase() = replace("Partial", "")

    private fun TypeName.asString(): String? = when (this) {
        is ClassName -> simpleName
        is ParameterizedTypeName -> this.typeArguments.first().asString()
        else -> null
    }

    private fun FileSpec.Builder.generateComments() =
        addFileComment(
            """
            | ----------
            | - WARNING -
            | ----------
            | This code is generated AND MUST NOT BE EDITED
            | ----------
            """.trimMargin(),
        )

    data class FunctionParameter(
        val parameterName: String,
        val nullable: Boolean,
        val type: TypeName,
    )
}
