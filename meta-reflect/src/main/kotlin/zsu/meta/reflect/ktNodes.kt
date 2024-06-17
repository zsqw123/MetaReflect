package zsu.meta.reflect

import zsu.cacheable.Cacheable
import zsu.meta.reflect.impl.k.MKFunctionImpl
import zsu.meta.reflect.impl.k.MKTypeImpl
import zsu.meta.reflect.impl.k.MKTypeParameterContainer
import zsu.meta.reflect.impl.type.MRKTypeParameterImpl
import kotlin.metadata.*
import kotlin.reflect.KTypeParameter

interface KtElement<T : Any> : MElement {
    /** represented kotlin metadata element. */
    val asKm: T
}

interface MKClass : MClass, KtElement<KmClass>

/**
 * Represents a Kotlin package fragment that contains top-level functions, properties and type aliases.
 * [MClass] has their own data, so all classes are not included in [MFile].
 */
interface MFile : KtElement<KmPackage>, MetadataContainer, KFileAdapter

class MLambda(
    override val asKm: KmLambda
) : KtElement<KmLambda>, MetadataContainer {
    val function: MFunction = MKFunctionImpl(null, asKm.function)
}

interface MKConstructor : MConstructor, KtElement<KmConstructor>


interface MKFunction : MFunction, KtElement<KmFunction>

interface MKProperty : MProperty, KtElement<KmProperty>

interface MTypeAlias : MMember<KmTypeAlias>, KtElement<KmTypeAlias> {
    val typeParameters: List<MTypeParameter>

    /** @see [KmTypeAlias.underlyingType] */
    val underlyingType: MType

    /** @see [KmTypeAlias.expandedType] */
    val expandedType: MType
}

interface MKType : MType, KTypeAdapter, KtElement<KmType>

class MKClassClassifier(override val asKm: KmClassifier) : MClassClassifier, KtElement<KmClassifier> {
    /** same as [Class.getName] */
    override val jName: JClassName = when (asKm) {
        is KmClassifier.Class -> asKm.name.asJClass
        is KmClassifier.TypeAlias -> asKm.name.asJClass
        else -> error("unexpected KmClassifier: $asKm")
    }
    override val asJr: Class<*> @Cacheable get() = Class.forName(jName)
}

class MKTypeParameterClassifier(
    override val asKm: KmClassifier.TypeParameter
) : MTypeParameterClassifier, KtElement<KmClassifier.TypeParameter> {
    val id = asKm.id
}

class MKTypeParameter(
    override val asKm: KmTypeParameter,
    private val parameterContainer: MKTypeParameterContainer,
) : MTypeParameter, KtElement<KmTypeParameter>, KReflectAdapter<KTypeParameter> {
    override val asKr: KTypeParameter @Cacheable get() = MRKTypeParameterImpl(asKm, parameterContainer)
}

class MKValueParameter(
    override val asKm: KmValueParameter,
    private val parameterContainer: MKTypeParameterContainer,
) : MValueParameter, KtElement<KmValueParameter> {
    override val name: String = asKm.name
    override val type: MType @Cacheable get() = MKTypeImpl(asKm.type, parameterContainer)

    override val varargElementType: MType?
        @Cacheable get() = asKm.varargElementType?.let { MKTypeImpl(it, parameterContainer) }
}
