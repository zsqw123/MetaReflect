package zsu.meta.reflect.impl.type

import kotlin.metadata.KmTypeParameter
import kotlin.metadata.isReified
import zsu.meta.reflect.impl.asKVariance
import zsu.meta.reflect.impl.k.MKTypeParameterContainer
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVariance

internal class MRKTypeParameterImpl(
    typeParameter: KmTypeParameter,
    typeParameterContainer: MKTypeParameterContainer,
) : KTypeParameter {
    override val isReified: Boolean = typeParameter.isReified
    override val name: String = typeParameter.name
    override val upperBounds: List<KType> = typeParameter.upperBounds.map {
        MRKTypeImpl(it, typeParameterContainer)
    }
    override val variance: KVariance = typeParameter.variance.asKVariance()
}
