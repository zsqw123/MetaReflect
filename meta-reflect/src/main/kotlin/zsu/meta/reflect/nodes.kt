package zsu.meta.reflect

import kotlinx.metadata.*
import kotlinx.metadata.jvm.fieldSignature
import kotlinx.metadata.jvm.signature
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type
import org.objectweb.asm.Type as AsmType
import java.lang.reflect.Type as JavaType

interface MElement<T : Any> {
    val asKm: T
}

/**
 * quite same apis with [KmDeclarationContainer], maybe you take it as a wrap for that.
 */
interface MClassLike<T : KmDeclarationContainer> : MElement<T>, JavaClassReflectAdapter {
    val functions: List<MFunction>
    val properties: List<MProperty>
    val typeAliases: List<MTypeAlias>
}

interface JavaReflectAdapter<J> {
    val asJr: J
}

interface JavaClassReflectAdapter : JavaReflectAdapter<Class<*>>
interface JavaConstructorReflectAdapter : JavaReflectAdapter<Constructor<*>>
interface JavaMethodReflectAdapter : JavaReflectAdapter<Method?>
interface JavaFieldReflectAdapter : JavaReflectAdapter<Field>


sealed interface MetadataContainer

abstract class AbsMDeclaration<T : KmDeclarationContainer> : MClassLike<T>, MetadataContainer {
    override val functions: List<MFunction> by lazy { asKm.functions.map { MFunction(this, it) } }
    override val properties: List<MProperty> by lazy { asKm.properties.map { MProperty(this, it) } }
    override val typeAliases: List<MTypeAlias> by lazy { asKm.typeAliases.map { MTypeAlias(this, it) } }
}

class MClass(
    override val asJr: Class<*>,
    override val asKm: KmClass,
) : AbsMDeclaration<KmClass>(), JavaReflectAdapter<Class<*>> {
    /** same as [Class.getName] */
    val jName: JClassName = asKm.name.asJClass

    val typeParameters: List<MTypeParameter> by lazy {
        asKm.typeParameters.map { MTypeParameter(it) }
    }

    val supertypes: List<MType> by lazy {
        asKm.supertypes.map { MType(it) }
    }

    val constructors: List<MConstructor> by lazy {
        asKm.constructors.map { MConstructor(this, it) }
    }

    val companionObjectName: String? = asKm.companionObject
    val nestedClassNames: List<String> = asKm.nestedClasses
    val enumEntryNames: List<String> = asKm.enumEntries

    val sealedSubclasses: List<JClassName> by lazy {
        asKm.sealedSubclasses.map { it.asJClass }
    }

    val inlineClassUnderlyingPropertyName: String? = asKm.inlineClassUnderlyingPropertyName
    val inlineClassUnderlyingType: MType? = asKm.inlineClassUnderlyingType?.let { MType(it) }

    @ExperimentalContextReceivers
    val contextReceiverTypes: List<MType> by lazy {
        asKm.contextReceiverTypes.map { MType(it) }
    }
}

/**
 * Represents a Kotlin package fragment that contains top-level functions, properties and type aliases.
 * [MClass] has their own data, so all classes are not included in [MFile].
 */
class MFile(
    override val asJr: Class<*>,
    override val asKm: KmPackage,
) : AbsMDeclaration<KmPackage>(), JavaClassReflectAdapter

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
) : MMember<KmFunction>, JavaMethodReflectAdapter {
    val name = asKm.name
    val typeParameters: List<MTypeParameter> by lazy {
        asKm.typeParameters.map { MTypeParameter(it) }
    }
    val receiverType: MType? = asKm.receiverParameterType?.let { MType(it) }

    @ExperimentalContextReceivers
    val contextReceiverTypes: List<MType> by lazy {
        asKm.contextReceiverTypes.map { MType(it) }
    }

    val valueParameters: List<MValueParameter> by lazy {
        asKm.valueParameters.map { MValueParameter(it) }
    }

    val returnType: MType = MType(asKm.returnType)

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
}

class MProperty(
    override val parent: MClassLike<*>,
    override val asKm: KmProperty,
) : MMember<KmProperty>, JavaFieldReflectAdapter {
    val name = asKm.name

    val typeParameters: List<MTypeParameter> by lazy {
        asKm.typeParameters.map { MTypeParameter(it) }
    }
    val receiverType: MType? = asKm.receiverParameterType?.let { MType(it) }

    @ExperimentalContextReceivers
    val contextReceiverTypes: List<MType> by lazy {
        asKm.contextReceiverTypes.map { MType(it) }
    }

    val setterParameter: MValueParameter? = asKm.setterParameter?.let { MValueParameter(it) }
    val returnType: MType = MType(asKm.returnType)

    override val asJr: Field by lazy {
        val parentClass = parent.asJr
        val jvmName = asKm.fieldSignature?.name ?: throw IllegalStateException(
            "cannot read signature of $asKm. class: ${parentClass.name}"
        )
        parentClass.declaredFields.first { it.name == jvmName }
    }
}

class MTypeAlias(
    override val parent: MClassLike<*>,
    override val asKm: KmTypeAlias,
) : MMember<KmTypeAlias>

class MType(override val asKm: KmType) : MElement<KmType>, JavaReflectAdapter<JavaType> {
    val classifier: MClassifier = when (val classifier = asKm.classifier) {
        is KmClassifier.Class, is KmClassifier.TypeAlias -> MClassClassifier(classifier)
        is KmClassifier.TypeParameter -> MTypeParameterClassifier(classifier)
    }

    val arguments: List<MTypeProjection> by lazy {
        asKm.arguments.map {
            if (it.type == null || it.variance == null) MStarType else MTypeWithVariance(it)
        }
    }

    override val asJr: Type
        get() = TODO("Not yet implemented")
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

class MTypeParameter(override val asKm: KmTypeParameter) : MElement<KmTypeParameter>

class MValueParameter(override val asKm: KmValueParameter) : MElement<KmValueParameter>

sealed interface MTypeProjection : MElement<KmTypeProjection>

data class MTypeWithVariance(override val asKm: KmTypeProjection) : MTypeProjection {
    val variance = when (asKm.variance!!) {
        KmVariance.INVARIANT -> MVariance.INVARIANT
        KmVariance.IN -> MVariance.IN
        KmVariance.OUT -> MVariance.OUT
    }
    val type = MType(asKm.type!!)
}

enum class MVariance(override val asKm: KmVariance) : MElement<KmVariance> {
    INVARIANT(KmVariance.INVARIANT),
    IN(KmVariance.IN),
    OUT(KmVariance.OUT),
}

data object MStarType : MTypeProjection {
    override val asKm: KmTypeProjection = KmTypeProjection.STAR
}
