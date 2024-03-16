package zsu.meta.reflect.impl

import kotlinx.metadata.KmTypeParameter
import kotlinx.metadata.isReified
import zsu.meta.reflect.TypeParameterContainer
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVariance

internal class MKTypeParameterImpl(
    typeParameter: KmTypeParameter,
    typeParameterContainer: TypeParameterContainer,
) : KTypeParameter {
    override val isReified: Boolean = typeParameter.isReified
    override val name: String = typeParameter.name
    override val upperBounds: List<KType> = typeParameter.upperBounds.map {
        MKTypeImpl(it, typeParameterContainer)
    }
    override val variance: KVariance = typeParameter.variance.asKVariance()
}
