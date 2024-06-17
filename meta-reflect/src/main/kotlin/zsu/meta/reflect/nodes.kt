package zsu.meta.reflect

import kotlin.metadata.*
import zsu.meta.reflect.impl.type.WildcardTypeImpl
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.WildcardType
import java.lang.reflect.Type as JavaType

interface MElement

/**
 * quite same apis with [KmDeclarationContainer], maybe you take it as a wrap for that.
 */
interface MClassLike : MElement, JavaClassReflectAdapter {
    val functions: List<MFunction>
    val properties: List<MProperty>
    val typeAliases: List<MTypeAlias>

    /** interop to [Class] **/
    val methods: Array<Method> /** [Class.getDeclaredMethods] */
    val fields: Array<Field> /** [Class.getDeclaredFields] */
    val enumEntries: Array<Enum<*>> /** [Class.getEnumConstants] */
    val annotations: Array<Annotation> /** [Class.getDeclaredAnnotations] */
}

sealed interface MetadataContainer

interface MClass : MClassLike,
    MetadataContainer, KClassAdapter {
    /** same as [Class.getName] */
    val jName: JClassName
    val typeParameters: List<MTypeParameter>
    val supertypes: List<MType>
    val constructors: List<MConstructor>
    val companionObjectName: SimpleName?
    val nestedClassNames: List<SimpleName>
    val enumEntryNames: List<SimpleName>
    val sealedSubclassNames: List<JClassName>
    val inlineClassUnderlyingPropertyName: SimpleName?
    val inlineClassUnderlyingType: MType?

    @ExperimentalContextReceivers
    val contextReceiverTypes: List<MType>

    /** interop to [Class] **/
    val sealedSubclasses: List<MClass>
    val companionObjectClass: MClass?
    val nestedClasses: List<MClass>
}

/**
 * member of class/file, such as [MFunction] or [MProperty]
 */
interface MMember<T : Any> : MElement {
    /**
     * returns null if no declaration parent or synthetic declaration.
     */
    val parent: MClassLike?
}

interface MConstructor : MMember<KmConstructor>, JavaConstructorReflectAdapter {
    val valueParameters: List<MValueParameter>
}

interface MFunction : MMember<KmFunction>, JavaMethodReflectAdapter {
    val name :String
    val typeParameters: List<MTypeParameter>
    val receiverType: MType?

    @ExperimentalContextReceivers
    val contextReceiverTypes: List<MType>

    val valueParameters: List<MValueParameter>
    val returnType: MType
}

interface MProperty : MMember<KmProperty>, JavaFieldReflectAdapter {
    val name: String

    val typeParameters: List<MTypeParameter>
    val receiverType: MType?

    @ExperimentalContextReceivers
    val contextReceiverTypes: List<MType>

    val setterParameter: MValueParameter?
    val returnType: MType
}

interface MType : MElement, JavaTypeAdapter {
    val classifier: MClassifier
    val arguments: List<MTypeProjection>
}

sealed interface MClassifier

interface MClassClassifier : MElement, MClassifier, JavaClassReflectAdapter {
    /** same as [Class.getName] */
    val jName: JClassName
}

interface MTypeParameterClassifier : MElement, MClassifier

interface MTypeParameter : MElement

interface MValueParameter : MElement {
    val name: String
    val type: MType
    val varargElementType: MType?
}

sealed interface MTypeProjection : JavaReflectAdapter<JavaType>

enum class MVariance {
    IN, OUT,
}

data class MTypeNoVariance(
    val type: MType,
) : MTypeProjection {
    override val asJr: JavaType by lazy { type.asJr }
}

data class MTypeWithVariance(
    val variance: MVariance, val type: MType,
) : MTypeProjection {
    override val asJr: WildcardType by lazy {
        val rawType = type.asJr
        if (variance == MVariance.OUT) WildcardTypeImpl(rawType, null)
        else WildcardTypeImpl(null, rawType)
    }
}

data object MStarType : MTypeProjection {
    override val asJr: WildcardType = WildcardTypeImpl.STAR
}
