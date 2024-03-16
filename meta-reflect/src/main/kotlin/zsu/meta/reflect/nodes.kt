package zsu.meta.reflect

import kotlinx.metadata.*

interface MElement<T : Any> {
    val asKm: T
}

/**
 * quite same apis with [KmDeclarationContainer], it just a wrap for that.
 */
interface MDeclaration<T : KmDeclarationContainer> : MElement<T> {
    val functions: List<MFunction>
    val properties: List<MProperty>
    val typeAliases: List<MTypeAlias>
}

sealed interface MetadataContainer

abstract class AbsMDeclaration<T : KmDeclarationContainer> : MDeclaration<T>, MetadataContainer {
    override val functions: List<MFunction> by lazy { asKm.functions.map { MFunction(this, it) } }
    override val properties: List<MProperty> by lazy { asKm.properties.map { MProperty(this, it) } }
    override val typeAliases: List<MTypeAlias> by lazy { asKm.typeAliases.map { MTypeAlias(this, it) } }
}

class MClass(
    override val asKm: KmClass,
) : AbsMDeclaration<KmClass>() {
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
    override val asKm: KmPackage,
) : AbsMDeclaration<KmPackage>()

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
    val parent: MDeclaration<*>?
}

class MConstructor(
    override val parent: MDeclaration<*>,
    override val asKm: KmConstructor,
) : MMember<KmConstructor> {
    val valueParameters: List<MValueParameter> by lazy {
        asKm.valueParameters.map { MValueParameter(it) }
    }
}

/**
 * @param parent returns null when function is a lambda generated.
 */
class MFunction(
    override val parent: MDeclaration<*>?,
    override val asKm: KmFunction,
) : MMember<KmFunction> {
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
}

class MProperty(
    override val parent: MDeclaration<*>,
    override val asKm: KmProperty,
) : MMember<KmProperty>

class MTypeAlias(
    override val parent: MDeclaration<*>,
    override val asKm: KmTypeAlias,
) : MMember<KmTypeAlias>

class MType(override val asKm: KmType) : MElement<KmType> {
    val classifier: MClassifier = when (val classifier = asKm.classifier) {
        is KmClassifier.Class, is KmClassifier.TypeAlias -> MClassClassifier(classifier)
        is KmClassifier.TypeParameter -> MTypeParameterClassifier(classifier)
    }


}

sealed interface MClassifier

class MClassClassifier(override val asKm: KmClassifier) : MElement<KmClassifier>, MClassifier {
    /** same as [Class.getName] */
    val jName: JClassName = when (asKm) {
        is KmClassifier.Class -> asKm.name.asJClass
        is KmClassifier.TypeAlias -> asKm.name.asJClass
        else -> error("unexpected KmClassifier: $asKm")
    }
}

class MTypeParameterClassifier(
    override val asKm: KmClassifier.TypeParameter
) : MElement<KmClassifier.TypeParameter>, MClassifier

class MTypeParameter(override val asKm: KmTypeParameter) : MElement<KmTypeParameter>

class MValueParameter(override val asKm: KmValueParameter) : MElement<KmValueParameter>
