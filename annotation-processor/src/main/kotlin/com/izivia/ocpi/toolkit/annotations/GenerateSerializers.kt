package com.izivia.ocpi.toolkit.annotations

import kotlin.reflect.KClass

/**
 * Defines a custom serializer for a specific field within a class.
 *
 * This annotation is used in conjunction with [CustomFieldSerializer] to specify which serializer
 * should be used for a particular field when generating surrogate classes.
 *
 * @property name The name of the field (property) to apply the serializer to
 * @property serializer The KSerializer class to use for this field
 *
 * @see CustomFieldSerializer
 * @see GenerateSerializers
 */
@Target()
@Retention(AnnotationRetention.SOURCE)
annotation class FieldSerializer(
    val name: String,
    val serializer: KClass<*>,
)

/**
 * Groups custom field serializers for a specific target class.
 *
 * This annotation allows you to specify multiple custom serializers for different fields
 * within a single class. It's particularly useful when you need fine-grained control
 * over how specific fields are serialized/deserialized.
 *
 * @property targetClass The class for which field serializers are being defined
 * @property fieldSerializer Array of [FieldSerializer] annotations defining custom serializers for specific fields
 *
 * @see FieldSerializer
 * @see GenerateSerializers
 */
@Target()
@Retention(AnnotationRetention.SOURCE)
annotation class CustomFieldSerializer(
    val targetClass: KClass<*>,
    val fieldSerializer: Array<FieldSerializer>,
)

/**
 * Defines a custom serializer for a specific class type.
 *
 * When specified, this serializer will be used for all occurrences of the class type
 * across all generated serializers, unless overridden by a field-specific serializer.
 * This is particularly useful for:
 * - Enum classes with custom serialization logic
 * - Built-in types like BigDecimal, Instant, etc.
 * - Third-party classes that need custom serialization
 *
 * @property targetClass The class to apply the serializer to
 * @property serializer The KSerializer class to use for this class
 *
 * @see GenerateSerializers
 */
@Target()
@Retention(AnnotationRetention.SOURCE)
annotation class CustomClassSerializer(
    val targetClass: KClass<*>,
    val serializer: KClass<*>,
)

/**
 * Generates kotlinx.serialization serializers for the specified classes using the surrogate pattern.
 *
 * This annotation triggers KSP processing to generate:
 * 1. A surrogate data class for each target class (with @Serializable)
 * 2. A serializer object that maps between the original class and its surrogate
 * 3. A SerializersModule containing all generated serializers
 *
 * The surrogate pattern is useful for:
 * - Adding serialization to classes you don't control (third-party libraries)
 * - Customizing serialization behavior without modifying the original class
 * - Applying custom serializers to specific fields or types
 *
 * @property classes Array of classes for which to generate serializers
 * @property excludedSerializersFromModule Array of classes whose serializers should not be
 *           included in the generated SerializersModule
 * @property customClassSerializers Custom serializers for specific class types, applied globally across all
 *           generated serializers unless overridden at the field level. Useful for enums, built-in types
 *           (BigDecimal, Instant), and third-party classes.
 * @property customFieldSerializers Field-specific custom serializers, allowing fine-grained control
 *           over how individual fields are serialized/deserialized
 *
 * @see CustomFieldSerializer
 * @see CustomClassSerializer
 *
 * Example usage:
 * ```
 * @GenerateSerializers(
 *     classes = [
 *         MyClass::class,
 *         AnotherClass::class
 *     ],
 *     customClassSerializers = [
 *         CustomClassSerializer(Status::class, StatusSerializer::class),
 *         CustomClassSerializer(BigDecimal::class, BigDecimalSerializer::class),
 *         CustomClassSerializer(Instant::class, InstantSerializer::class)
 *     ],
 *     customFieldSerializers = [
 *         CustomFieldSerializer(
 *             targetClass = MyClass::class,
 *             fieldSerializer = [
 *                 FieldSerializer("specialField", CustomFieldSerializer::class)
 *             ]
 *         )
 *     ]
 * )
 * class MySerializers
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateSerializers(
    val classes: Array<KClass<*>>,
    val excludedSerializersFromModule: Array<KClass<*>> = [],
    val customClassSerializers: Array<CustomClassSerializer> = [],
    val customFieldSerializers: Array<CustomFieldSerializer> = [],
)
