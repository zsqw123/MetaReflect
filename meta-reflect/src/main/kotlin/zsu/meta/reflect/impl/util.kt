package zsu.meta.reflect.impl

import kotlinx.metadata.KmVariance
import zsu.meta.reflect.MElement
import zsu.meta.reflect.MKTypeParameter
import zsu.meta.reflect.impl.k.MKTypeParameterContainer
import kotlin.reflect.KVariance

internal fun KmVariance.asKVariance(): KVariance = when (this) {
    KmVariance.INVARIANT -> KVariance.INVARIANT
    KmVariance.IN -> KVariance.IN
    KmVariance.OUT -> KVariance.OUT
}

internal fun MKTypeParameterContainer.parameterId(
    parent: MElement?, id: Int,
): MKTypeParameter {
    val typeParameters = typeParameters
    if (typeParameters.isEmpty()) {
        if (parent is MKTypeParameterContainer) return parent.getTypeParameter(id)
        error("type parameter $id not found in $this which inside of $parent")
    }
    return typeParameters.first { it.asKm.id == id }
}

