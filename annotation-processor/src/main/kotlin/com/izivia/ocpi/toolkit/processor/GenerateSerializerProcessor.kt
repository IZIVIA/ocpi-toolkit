package com.izivia.ocpi.toolkit.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.izivia.ocpi.toolkit.annotations.GenerateSerializers
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * KSP processor that generates kotlinx.serialization serializers using the surrogate pattern.
 *
 * This processor scans for classes annotated with @GenerateSerializers and generates:
 * 1. Surrogate data classes (with @Serializable) mirroring the target classes
 * 2. Serializer objects that map between original and surrogate classes
 * 3. A SerializersModule registering all generated serializers
 *
 * The surrogate pattern allows serialization of classes without modifying them directly,
 * which is particularly useful for third-party classes or when you want to keep
 * serialization logic separate from domain models.
 *
 * @property codeGenerator KSP code generator for creating new source files
 * @property logger KSP logger for diagnostic messages
 */
class GenerateSerializerProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    /**
     * Maps class qualified names to their custom serializer qualified names.
     * Populated during annotation processing from CustomClassSerializer annotations.
     * This includes enum serializers, built-in type serializers (BigDecimal, Instant),
     * and any other custom class-level serializers.
     */
    private val classSerializersMap = mutableMapOf<String, String>()

    /**
     * Maps field identifiers to their custom serializer qualified names.
     * Key format: "fully.qualified.ClassName.fieldName"
     * Populated during annotation processing from CustomFieldSerializer and FieldSerializer annotations.
     */
    private val fieldSerializersMap = mutableMapOf<String, String>()

    /**
     * Main processing entry point called by KSP.
     *
     * Finds all symbols annotated with @GenerateSerializers and processes them.
     * Returns symbols that couldn't be processed (e.g., due to missing dependencies).
     *
     * @param resolver KSP resolver for querying symbols
     * @return List of symbols that couldn't be processed and should be deferred
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(GenerateSerializers::class.qualifiedName!!)
        val unableToProcess = symbols.filter { !it.validate() }.toList()

        symbols
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach { processGenerateSerializers(it as KSClassDeclaration, resolver) }

        return unableToProcess
    }

    /**
     * Processes a single @GenerateSerializers annotation.
     *
     * Extracts configuration from the annotation (target classes, custom serializers, etc.),
     * populates the serializer maps, and generates serializers for each target class.
     *
     * @param declaration The class declaration annotated with @GenerateSerializers
     * @param resolver KSP resolver for querying symbols
     */
    private fun processGenerateSerializers(
        declaration: KSClassDeclaration,
        resolver: Resolver,
    ) {
        val annotation = declaration.annotations
            .firstOrNull { it.shortName.asString() == "GenerateSerializers" }

        if (annotation == null) {
            logger.warn("Annotation @GenerateSerializers not found on ${declaration.qualifiedName?.asString()}")
            return
        }

        @Suppress("UNCHECKED_CAST")
        val classes = (
            annotation.arguments
                .first { it.name?.asString() == "classes" }
                .value as? ArrayList<KSType>
            )?.mapNotNull { it.declaration.qualifiedName?.asString() } ?: emptyList()

        @Suppress("UNCHECKED_CAST")
        val excludedSerializersFromModule = (
            annotation.arguments
                .firstOrNull { it.name?.asString() == "excludedSerializersFromModule" }
                ?.value as? ArrayList<KSType>
            )?.mapNotNull { it.declaration.qualifiedName?.asString() } ?: emptyList()

        val customClassSerializers = (
            annotation.arguments
                .firstOrNull { it.name?.asString() == "customClassSerializers" }
                ?.value as? ArrayList<*>
            ) ?: emptyList()

        val customFieldSerializers = (
            annotation.arguments
                .firstOrNull { it.name?.asString() == "customFieldSerializers" }
                ?.value as? ArrayList<*>
            ) ?: emptyList()

        classSerializersMap.clear()
        customClassSerializers.forEach { annotationValue ->
            if (annotationValue is KSAnnotation) {
                val targetClass = annotationValue.arguments
                    .firstOrNull { it.name?.asString() == "targetClass" }
                    ?.value as? KSType

                val serializerClass = annotationValue.arguments
                    .firstOrNull { it.name?.asString() == "serializer" }
                    ?.value as? KSType

                if (targetClass != null && serializerClass != null) {
                    val targetQualifiedName = targetClass.declaration.qualifiedName?.asString()
                    val serializerQualifiedName = serializerClass.declaration.qualifiedName?.asString()

                    if (targetQualifiedName != null && serializerQualifiedName != null) {
                        classSerializersMap[targetQualifiedName] = serializerQualifiedName
                        logger.info("  - Registered class serializer: $targetQualifiedName -> $serializerQualifiedName")
                    }
                }
            }
        }

        fieldSerializersMap.clear()
        customFieldSerializers.forEach { customFieldSerializerAnnotation ->
            if (customFieldSerializerAnnotation is KSAnnotation) {
                val targetClass = customFieldSerializerAnnotation.arguments
                    .firstOrNull { it.name?.asString() == "targetClass" }
                    ?.value as? KSType

                val className = targetClass?.declaration?.qualifiedName?.asString()

                val fieldSerializer = customFieldSerializerAnnotation.arguments
                    .firstOrNull { it.name?.asString() == "fieldSerializer" }
                    ?.value as? ArrayList<*>

                if (className != null && fieldSerializer != null) {
                    fieldSerializer.forEach { fieldSerializerAnnotation ->
                        if (fieldSerializerAnnotation is KSAnnotation) {
                            val fieldName = fieldSerializerAnnotation.arguments
                                .firstOrNull { it.name?.asString() == "name" }
                                ?.value as? String

                            val serializerClass = fieldSerializerAnnotation.arguments
                                .firstOrNull { it.name?.asString() == "serializer" }
                                ?.value as? KSType

                            if (fieldName != null && serializerClass != null) {
                                val serializerQualifiedName = serializerClass.declaration.qualifiedName?.asString()
                                if (serializerQualifiedName != null) {
                                    val key = "$className.$fieldName"
                                    fieldSerializersMap[key] = serializerQualifiedName
                                    logger.info("  - Registered field serializer: $key -> $serializerQualifiedName")
                                }
                            }
                        }
                    }
                }
            }
        }

        logger.info(
            "Processing @GenerateSerializers for ${classes.size} classes with " +
                "${classSerializersMap.size} class serializers and ${fieldSerializersMap.size} field serializers",
        )

        val generatedSerializers = mutableListOf<Pair<String, String>>()

        classes.forEach { className ->
            logger.info("  - Processing: $className")

            val targetClass = resolver.getClassDeclarationByName(
                resolver.getKSNameFromString(className),
            )

            if (targetClass == null) {
                logger.error("Cannot find class: $className", declaration)
            } else {
                generateSurrogateAndSerializer(targetClass)
                val simpleName = targetClass.simpleName.asString()
                generatedSerializers.add(className to "${simpleName}Serializer")
            }
        }

        if (generatedSerializers.isNotEmpty()) {
            generateSerializersModule(
                declaration,
                generatedSerializers.filterNot { excludedSerializersFromModule.contains(it.first) },
            )
        }
    }

    /**
     * Generates a surrogate class and its corresponding serializer for a target class.
     *
     * Creates:
     * 1. A @Serializable surrogate data class with the same properties as the target
     * 2. A serializer object implementing KSerializer using the mapped() function
     * 3. Appropriate @UseSerializers file-level annotation when needed
     *
     * The generated serializer handles conversion between the target class and its surrogate
     * during serialization/deserialization.
     *
     * @param targetClass The class for which to generate a serializer
     */
    private fun generateSurrogateAndSerializer(
        targetClass: KSClassDeclaration,
    ) {
        val packageName = targetClass.packageName.asString()
        val className = targetClass.simpleName.asString()
        val surrogateName = "${className}Surrogate"
        val serializerName = "${className}Serializer"

        val properties = targetClass.getAllProperties()
            .filter { it.hasPublicGetter() }
            .toList()

        if (properties.isEmpty()) {
            logger.warn("No properties found for $className")
            return
        }

        val file = FileSpec.builder(packageName, serializerName)
            .addImport("com.izivia.ocpi.toolkit.integrations.kotlinx.serialization", "mapped")

        val surrogateClass = TypeSpec.classBuilder(surrogateName)
            .addModifiers(KModifier.DATA)
            .addAnnotation(
                AnnotationSpec.builder(ClassName("kotlinx.serialization", "Serializable"))
                    .build(),
            )
            .addAnnotation(
                AnnotationSpec.builder(ClassName("kotlinx.serialization", "SerialName"))
                    .addMember("%S", targetClass.qualifiedName!!.asString())
                    .build(),
            )

        val constructorBuilder = FunSpec.constructorBuilder()
        val toSurrogateParams = mutableListOf<String>()
        val fromSurrogateParams = mutableListOf<String>()

        val serializersForFile = mutableSetOf<ClassName>()
        val serializersUsedDirectlyOnProperties = mutableSetOf<ClassName>()

        properties.forEach { prop ->
            val propName = prop.simpleName.asString()
            val propType = prop.type.resolve()
            val propTypeName = propType.toTypeName()

            logger.info("  Property: $propName : ${propType.declaration.qualifiedName?.asString()}")

            constructorBuilder.addParameter(propName, propTypeName)

            val propertySpecBuilder = PropertySpec.builder(propName, propTypeName)
                .initializer(propName)

            val fieldKey = "${targetClass.qualifiedName!!.asString()}.$propName"
            val customFieldSerializer = fieldSerializersMap[fieldKey]?.let { serializerFullName ->
                serializerFullNameToClassName(serializerFullName)
            }

            val serializerForProperty = customFieldSerializer ?: getSerializerForProperty(propType)

            if (serializerForProperty != null) {
                logger.info(
                    "    -> Adding @Serializable(with = $serializerForProperty::class) " +
                        "[${serializerForProperty.canonicalName}]",
                )
                propertySpecBuilder.addAnnotation(
                    AnnotationSpec.builder(ClassName("kotlinx.serialization", "Serializable"))
                        .addMember("with = %T::class", serializerForProperty)
                        .build(),
                )
                serializersUsedDirectlyOnProperties.add(serializerForProperty)
            }

            surrogateClass.addProperty(propertySpecBuilder.build())

            val serializersInType = if (customFieldSerializer != null) {
                setOf(customFieldSerializer)
            } else {
                collectAllSerializers(propType)
            }

            if (serializersInType.isNotEmpty()) {
                logger.info(
                    "    -> Collected serializers for @UseSerializers: " +
                        serializersInType.joinToString { it.canonicalName },
                )
            }
            serializersForFile.addAll(serializersInType)

            toSurrogateParams.add("$propName = it.$propName")
            fromSurrogateParams.add("$propName = it.$propName")
        }

        surrogateClass.primaryConstructor(constructorBuilder.build())

        val serializersOnlyForUseSerializers = serializersForFile - serializersUsedDirectlyOnProperties

        if (serializersOnlyForUseSerializers.isNotEmpty()) {
            val useSerializersBuilder = AnnotationSpec.builder(
                ClassName("kotlinx.serialization", "UseSerializers"),
            ).useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)

            serializersOnlyForUseSerializers.forEach { serializerClass ->
                useSerializersBuilder.addMember("%T::class", serializerClass)
            }

            file.addAnnotation(useSerializersBuilder.build())
        }

        file.addType(surrogateClass.build())

        val targetClassName = targetClass.toClassName()
        val serializerType = ClassName("kotlinx.serialization", "KSerializer")
            .parameterizedBy(targetClassName)

        val serializerObject = TypeSpec.objectBuilder(serializerName)
            .addSuperinterface(
                serializerType,
                delegate = CodeBlock.builder()
                    .add(
                        "%T.serializer().mapped(\n",
                        ClassName(packageName, surrogateName),
                    )
                    .indent()
                    .add("convertForEncoding = {\n")
                    .indent()
                    .add("%T(\n", ClassName(packageName, surrogateName))
                    .indent()
                    .add(toSurrogateParams.joinToString(",\n"))
                    .add("\n")
                    .unindent()
                    .add(")")
                    .add("\n")
                    .unindent()
                    .add("},\n")
                    .add("convertForDecoding = {\n")
                    .indent()
                    .add("%T(\n", targetClassName)
                    .indent()
                    .add(fromSurrogateParams.joinToString(",\n"))
                    .add("\n")
                    .unindent()
                    .add(")")
                    .add("\n")
                    .unindent()
                    .add("}\n")
                    .unindent()
                    .add(")")
                    .build(),
            )

        file.addType(serializerObject.build())

        val dependencies = if (targetClass.containingFile != null) {
            Dependencies(true, targetClass.containingFile!!)
        } else {
            Dependencies(false)
        }

        file.build().writeTo(codeGenerator, dependencies)

        logger.info("Generated $serializerName for $className")
    }

    /**
     * Determines the appropriate serializer for a property, if any.
     *
     * Checks (in order):
     * 1. If the property type has a custom class serializer (from customClassSerializers)
     * 2. If the type is a generated custom class requiring a serializer
     *
     * Returns null for standard Kotlin types and collections (handled by kotlinx.serialization).
     *
     * @param propType The resolved type of the property
     * @return ClassName of the serializer to use, or null if no custom serializer is needed
     */
    private fun getSerializerForProperty(propType: KSType): ClassName? {
        val declaration = propType.declaration
        val qualifiedName = declaration.qualifiedName?.asString() ?: return null

        // Skip standard Kotlin collections
        if (qualifiedName.startsWith("kotlin.collections.")) {
            return null
        }

        // Skip standard Kotlin and Java types that don't have custom serializers
        if (qualifiedName.startsWith("kotlin.") || qualifiedName.startsWith("java.lang.")) {
            // Check if there's a custom class serializer registered for this type
            // (e.g., BigDecimal, Instant which are java types but have custom serializers)
            return classSerializersMap[qualifiedName]?.let { serializerFullName ->
                serializerFullNameToClassName(serializerFullName)
            }
        }

        // Check if there's a custom class serializer registered for this type
        classSerializersMap[qualifiedName]?.let { serializerFullName ->
            return serializerFullNameToClassName(serializerFullName)
        }

        // For custom classes without @Serializable, use the generated serializer
        if (declaration is KSClassDeclaration &&
            declaration.classKind == ClassKind.CLASS &&
            declaration.annotations.none { it.shortName.asString() == "Serializable" }
        ) {
            val packageName = declaration.packageName.asString()
            val simpleName = declaration.simpleName.asString()
            return ClassName(packageName, "${simpleName}Serializer")
        }

        return null
    }

    /**
     * Converts a fully qualified serializer name to a ClassName.
     *
     * @param serializerFullName Fully qualified name like "com.example.MySerializer"
     * @return ClassName with proper package and simple name
     */
    private fun serializerFullNameToClassName(serializerFullName: String): ClassName {
        val parts = serializerFullName.split(".")
        return ClassName(parts.dropLast(1).joinToString("."), parts.last())
    }

    /**
     * Recursively collects all serializers needed for a type and its type arguments.
     *
     * This function traverses the type tree to find all custom serializers that should be
     * included in the @UseSerializers annotation. It handles:
     * - Direct type serializers (e.g., for BigDecimal, Instant, custom enums, custom classes)
     * - Generic type arguments (e.g., T in List<T>, K, and V in Map<K,V>)
     *
     * Collections themselves don't need serializers as kotlinx.serialization handles them,
     * but their element types might need custom serializers.
     *
     * @param type The type to analyze
     * @return Set of all custom serializers needed for this type and its type arguments
     */
    private fun collectAllSerializers(type: KSType): Set<ClassName> {
        val serializers = mutableSetOf<ClassName>()

        val declaration = type.declaration
        val qualifiedName = declaration.qualifiedName?.asString()

        // Don't add serializers for collections themselves
        if (qualifiedName != null && !qualifiedName.startsWith("kotlin.collections.")) {
            getSerializerInfo(type)?.let { serializers.add(it) }
        }

        type.arguments.forEach { arg ->
            arg.type?.resolve()?.let { argType ->
                serializers.addAll(collectAllSerializers(argType))
            }
        }

        return serializers
    }

    /**
     * Gets serializer information for a specific type.
     *
     * Checks if the type requires a custom serializer and returns its ClassName.
     * Handles:
     * - Types with custom class serializers (from customClassSerializers)
     * - Generated custom class serializers
     *
     * Does NOT recurse into type arguments (that's handled by collectAllSerializers).
     *
     * @param type The type to check
     * @return ClassName of the serializer if one is needed, null otherwise
     */
    private fun getSerializerInfo(type: KSType): ClassName? {
        val qualifiedName = type.declaration.qualifiedName?.asString() ?: return null

        // Check if there's a custom class serializer registered for this type
        classSerializersMap[qualifiedName]?.let { serializerFullName ->
            logger.info("    Found custom class serializer for $qualifiedName: $serializerFullName")
            return serializerFullNameToClassName(serializerFullName)
        }

        val declaration = type.declaration

        // For custom classes without @Serializable, use the generated serializer
        if (declaration is KSClassDeclaration &&
            declaration.classKind == ClassKind.CLASS &&
            !qualifiedName.startsWith("kotlin.") &&
            !qualifiedName.startsWith("java.lang.") &&
            declaration.annotations.none { it.shortName.asString() == "Serializable" }
        ) {
            val packageName = declaration.packageName.asString()
            val simpleName = declaration.simpleName.asString()
            return ClassName(packageName, "${simpleName}Serializer")
        }

        return null
    }

    /**
     * Generates a SerializersModule containing all generated serializers.
     *
     * Creates a module with contextual serializers for each generated type, allowing
     * polymorphic serialization and deserialization when using Json with the module.
     *
     * The generated module is placed in a standard package and can be used like:
     * ```
     * val json = Json { serializersModule = generatedSerializersModule }
     * ```
     *
     * @param annotatedClass The class that was annotated with @GenerateSerializers
     * @param serializers List of (className, serializerName) pairs to include in the module
     */
    private fun generateSerializersModule(
        annotatedClass: KSClassDeclaration,
        serializers: List<Pair<String, String>>,
    ) {
        val packageName = "com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.generated"
        val moduleName = "GeneratedSerializersModule"

        val file = FileSpec.builder(packageName, moduleName)
            .addImport("kotlinx.serialization.modules", "SerializersModule")
            .addImport("kotlinx.serialization.modules", "contextual")

        serializers.forEach { (className, serializerName) ->
            val classPackage = className.substringBeforeLast(".")
            file.addImport(classPackage, className.substringAfterLast("."))
            file.addImport(classPackage, serializerName)
        }

        val moduleCode = CodeBlock.builder()
            .add("SerializersModule {\n")
            .indent()

        serializers.forEach { (className, serializerName) ->
            moduleCode.add(
                "contextual(%T::class, %L)\n",
                ClassName.bestGuess(className),
                serializerName,
            )
        }

        moduleCode.unindent()
            .add("}")

        val moduleProperty = PropertySpec.builder(
            "generatedSerializersModule",
            ClassName("kotlinx.serialization.modules", "SerializersModule"),
        )
            .initializer(moduleCode.build())
            .build()

        file.addProperty(moduleProperty)

        val dependencies = if (annotatedClass.containingFile != null) {
            Dependencies(true, annotatedClass.containingFile!!)
        } else {
            Dependencies(false)
        }

        file.build().writeTo(codeGenerator, dependencies)

        logger.info("Generated SerializersModule with ${serializers.size} serializers")
    }

    /**
     * Checks if a property has a public getter.
     *
     * Properties without public getters cannot be serialized, so we filter them out.
     * If no explicit getter is defined, Kotlin properties have a public getter by default.
     *
     * @return true if the property has a public getter, false otherwise
     */
    private fun KSPropertyDeclaration.hasPublicGetter(): Boolean {
        return getter?.modifiers?.none { it == Modifier.PRIVATE || it == Modifier.PROTECTED } ?: true
    }

    /**
     * Converts a KSType to a KotlinPoet TypeName.
     *
     * Handles:
     * - Built-in types (String, Int, Boolean, etc.)
     * - Custom classes and type aliases
     * - Generic types with type parameters (List<T>, Map<K,V>)
     * - Nullable types (marked with ?)
     *
     * This conversion is necessary because KotlinPoet uses TypeName for code generation,
     * while KSP works with KSType.
     *
     * @return TypeName representation of this type, preserving nullability
     */
    private fun KSType.toTypeName(): TypeName {
        val declaration = this.declaration

        val baseType = when (declaration.qualifiedName?.asString()) {
            "kotlin.String" -> STRING
            "kotlin.Int" -> INT
            "kotlin.Long" -> LONG
            "kotlin.Boolean" -> BOOLEAN
            "kotlin.Double" -> DOUBLE
            "kotlin.Float" -> FLOAT
            else -> {
                when (declaration) {
                    is KSClassDeclaration -> declaration.toClassName()
                    is KSTypeAlias -> declaration.toClassName()
                    else -> ClassName(
                        declaration.packageName.asString(),
                        declaration.simpleName.asString(),
                    )
                }
            }
        }

        val typeArguments = this.arguments
        val finalType = if (typeArguments.isNotEmpty()) {
            val typeParams = typeArguments.mapNotNull { arg ->
                arg.type?.resolve()?.toTypeName()
            }
            if (typeParams.isNotEmpty()) {
                baseType.parameterizedBy(typeParams)
            } else {
                baseType
            }
        } else {
            baseType
        }

        return if (this.isMarkedNullable) {
            finalType.copy(nullable = true)
        } else {
            finalType
        }
    }
}

/**
 * Provider for the GenerateSerializerProcessor.
 *
 * This class is the entry point for KSP to discover and instantiate the processor.
 * It's registered via META-INF/services and called by KSP during compilation.
 */
class GenerateSerializerProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return GenerateSerializerProcessor(
            environment.codeGenerator,
            environment.logger,
        )
    }
}
