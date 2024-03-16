package zsu.meta.reflect.impl

import kotlinx.metadata.KmTypeParameter
import kotlinx.metadata.isReified
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVariance

internal class MKTypeParameterImpl(
    typeParameter: KmTypeParameter,
) : KTypeParameter {
    override val isReified: Boolean = typeParameter.isReified
    override val name: String = typeParameter.name
    override val upperBounds: List<KType> = typeParameter.upperBounds.map { MKTypeImpl(it) }
    override val variance: KVariance = typeParameter.variance.asKVariance()
}
