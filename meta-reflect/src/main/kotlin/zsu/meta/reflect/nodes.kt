package zsu.meta.reflect

import kotlinx.metadata.*
import kotlinx.metadata.jvm.fieldSignature
import kotlinx.metadata.jvm.signature
import zsu.meta.reflect.impl.MKTypeImpl
import zsu.meta.reflect.impl.MKTypeParameterImpl
import zsu.meta.reflect.impl.WildcardTypeImpl
import zsu.meta.reflect.impl.parameterId
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.WildcardType
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import org.objectweb.asm.Type as AsmType
import java.lang.reflect.Type as JavaType

interface MElement<T : Any> {
    /** represented kotlin metadata element. */
    val asKm: T
}

/**
 * quite same apis with [KmDeclarationContainer], maybe you take it as a wrap for that.
 */
interface MClassLike<T : KmDeclarationContainer> : MElement<T>, JavaClassReflectAdapter {
    val functions: List<MFunction>
    val properties: List<MProperty>
    val typeAliases: List<MTypeAlias>

    /** interop to [Class] **/
    val methods: Array<Method>
    val fields: Array<Field>
}

interface TypeParameterContainer {
    fun getTypeParameter(id: Int): MTypeParameter
}

sealed interface MetadataContainer

interface MClass : MClassLike<KmClass>,
    MetadataContainer, KClassAdapter {
    /** same as [Class.getName] */
    val jName: JClassName
    val typeParameters: List<MTypeParameter>
    val supertypes: List<MType>
    val constructors: List<MConstructor>
    val companionObjectName: String?
    val nestedClassNames: List<String>
    val enumEntryNames: List<String>
    val sealedSubclassNames: List<JClassName>
    val inlineClassUnderlyingPropertyName: String?
    val inlineClassUnderlyingType: MType?

    @ExperimentalContextReceivers
    val contextReceiverTypes: List<MType>

    /** interop to [Class] **/
    val sealedSubclasses: List<Class<*>>
    val companionObjectClass: Class<*>?
    val nestedClasses: List<Class<*>>
}

/**
 * Represents a Kotlin package fragment that contains top-level functions, properties and type aliases.
 * [MClass] has their own data, so all classes are not included in [MFile].
 */
interface MFile : MClassLike<KmPackage>,
    MetadataContainer, KFileAdapter

class MLambda(
    override val asKm: KmLambda
) : MElement<KmLambda>, MetadataContainer {
    val function: MFunction = MFunction(null, asKm.function)
}

/**
 * member of class/file, such as [MFunction] or [MProperty]
 */
interface MMember<T : Any> : MElement<T> {
    /**
     * returns null if no declaration parent or synthetic declaration.
     */
    val parent: MClassLike<*>?
}

class MConstructor(
    override val parent: MClassLike<*>,
    override val asKm: KmConstructor,
) : MMember<KmConstructor>, JavaConstructorReflectAdapter {
    val valueParameters: List<MValueParameter> by lazy {
        asKm.valueParameters.map { MValueParameter(it) }
    }
    override val asJr: Constructor<*> by lazy {
        val parentClass = parent.asJr
        val descriptor = asKm.signature?.descriptor ?: throw IllegalStateException(
            "cannot read signature of $asKm. class: ${parentClass.name}"
        )
        parent.asJr.declaredConstructors.first {
            AsmType.getConstructorDescriptor(it) == descriptor
        }
    }
}

/**
 * @param parent returns null when function is a lambda generated.
 */
class MFunction(
    override val parent: MClassLike<*>?,
    override val asKm: KmFunction,
) : MMember<KmFunction>, JavaMethodReflectAdapter, TypeParameterContainer {
    val name = asKm.name
    val typeParameters: List<MTypeParameter> by lazy {
        asKm.typeParameters.map { MTypeParameter(it, this) }
    }
    val receiverType: MType? = asKm.receiverParameterType?.let { MType(it, this) }

    @ExperimentalContextReceivers
    val contextReceiverTypes: List<MType> by lazy {
        asKm.contextReceiverTypes.map { MType(it, this) }
    }

    val valueParameters: List<MValueParameter> by lazy {
        asKm.valueParameters.map { MValueParameter(it) }
    }

    val returnType: MType = MType(asKm.returnType, this)

    // null when lambda
    override val asJr: Method? by lazy {
        val parentClass = parent?.asJr ?: return@lazy null
        val descriptor = asKm.signature?.descriptor ?: throw IllegalStateException(
            "cannot read signature of $asKm. class: ${parentClass.name}"
        )
        parentClass.declaredMethods.first {
            AsmType.getMethodDescriptor(it) == descriptor
        }
    }

    override fun getTypeParameter(id: Int): MTypeParameter {
        return parameterId(typeParameters, parent, id)
    }

    override fun toString(): String {
        return "function $name"
    }
}

class MProperty(
    override val parent: MClassLike<*>,
    override val asKm: KmProperty,
) : MMember<KmProperty>, JavaFieldReflectAdapter, TypeParameterContainer {
    val name = asKm.name

    val typeParameters: List<MTypeParameter> by lazy {
        asKm.typeParameters.map { MTypeParameter(it, this) }
    }
    val receiverType: MType? = asKm.receiverParameterType?.let { MType(it, this) }

    @ExperimentalContextReceivers
    val contextReceiverTypes: List<MType> by lazy {
        asKm.contextReceiverTypes.map { MType(it, this) }
    }

    val setterParameter: MValueParameter? = asKm.setterParameter?.let { MValueParameter(it) }
    val returnType: MType = MType(asKm.returnType, this)

    override val asJr: Field by lazy {
        val parentClass = parent.asJr
        val jvmName = asKm.fieldSignature?.name ?: throw IllegalStateException(
            "cannot read signature of $asKm. class: ${parentClass.name}"
        )
        parentClass.declaredFields.first { it.name == jvmName }
    }

    override fun getTypeParameter(id: Int): MTypeParameter {
        return parameterId(typeParameters, parent, id)
    }

    override fun toString(): String {
        return "property $name"
    }
}

class MTypeAlias(
    override val parent: MClassLike<*>,
    override val asKm: KmTypeAlias,
) : MMember<KmTypeAlias>, TypeParameterContainer {
    val typeParameters: List<MTypeParameter> by lazy {
        asKm.typeParameters.map { MTypeParameter(it, this) }
    }

    /** @see [KmTypeAlias.underlyingType] */
    val underlyingType: MType by lazy { MType(asKm.underlyingType, this) }

    /** @see [KmTypeAlias.expandedType] */
    val expandedType: MType by lazy { MType(asKm.expandedType, this) }

    override fun getTypeParameter(id: Int): MTypeParameter {
        return parameterId(typeParameters, parent, id)
    }
}

class MType(
    override val asKm: KmType,
    private val parameterContainer: TypeParameterContainer,
) : MElement<KmType>, KTypeAdapter {
    val classifier: MClassifier = when (val classifier = asKm.classifier) {
        is KmClassifier.Class, is KmClassifier.TypeAlias -> MClassClassifier(classifier)
        is KmClassifier.TypeParameter -> MTypeParameterClassifier(classifier)
    }

    val arguments: List<MTypeProjection> by lazy {
        asKm.arguments.map {
            when {
                it.type == null || it.variance == null -> MStarType
                it.variance == KmVariance.INVARIANT -> MTypeNoVariance(it, parameterContainer)
                else -> MTypeWithVariance(it, parameterContainer)
            }
        }
    }

    override val asKr: KType by lazy { MKTypeImpl(asKm, parameterContainer) }
}

sealed interface MClassifier

class MClassClassifier(override val asKm: KmClassifier) :
    MElement<KmClassifier>, MClassifier, JavaClassReflectAdapter {
    /** same as [Class.getName] */
    val jName: JClassName = when (asKm) {
        is KmClassifier.Class -> asKm.name.asJClass
        is KmClassifier.TypeAlias -> asKm.name.asJClass
        else -> error("unexpected KmClassifier: $asKm")
    }
    override val asJr: Class<*> by lazy { Class.forName(jName) }
}

class MTypeParameterClassifier(
    override val asKm: KmClassifier.TypeParameter
) : MElement<KmClassifier.TypeParameter>, MClassifier

class MTypeParameter(
    override val asKm: KmTypeParameter,
    private val parameterContainer: TypeParameterContainer,
) : MElement<KmTypeParameter>, KReflectAdapter<KTypeParameter> {
    override val asKr: KTypeParameter by lazy { MKTypeParameterImpl(asKm, parameterContainer) }
}

class MValueParameter(override val asKm: KmValueParameter) : MElement<KmValueParameter>

sealed interface MTypeProjection : MElement<KmTypeProjection>, JavaReflectAdapter<JavaType>

data class MTypeNoVariance(
    override val asKm: KmTypeProjection, val parameterContainer: TypeParameterContainer,
) : MTypeProjection {
    val type = MType(asKm.type!!, parameterContainer)
    override val asJr: JavaType by lazy { type.asJr }
}

data class MTypeWithVariance(
    override val asKm: KmTypeProjection, val parameterContainer: TypeParameterContainer
) : MTypeProjection {
    val variance = when (val origin = asKm.variance!!) {
        KmVariance.IN -> MVariance.IN
        KmVariance.OUT -> MVariance.OUT
        else -> throw error("Must have variance! origin KmTypeProjection: $origin")
    }
    val type = MType(asKm.type!!, parameterContainer)
    override val asJr: WildcardType by lazy {
        val rawType = type.asJr
        if (variance == MVariance.OUT) WildcardTypeImpl(rawType, null)
        else WildcardTypeImpl(null, rawType)
    }
}

enum class MVariance(override val asKm: KmVariance) : MElement<KmVariance> {
    IN(KmVariance.IN),
    OUT(KmVariance.OUT),
}

data object MStarType : MTypeProjection {
    override val asKm: KmTypeProjection = KmTypeProjection.STAR
    override val asJr: WildcardType = WildcardTypeImpl.STAR
}
