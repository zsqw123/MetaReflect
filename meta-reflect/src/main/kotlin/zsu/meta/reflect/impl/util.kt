package zsu.meta.reflect.impl

import kotlinx.metadata.KmVariance
import zsu.meta.reflect.MElement
import zsu.meta.reflect.MTypeParameter
import zsu.meta.reflect.TypeParameterContainer
import kotlin.reflect.KVariance

internal fun KmVariance.asKVariance(): KVariance = when (this) {
    KmVariance.INVARIANT -> KVariance.INVARIANT
    KmVariance.IN -> KVariance.IN
    KmVariance.OUT -> KVariance.OUT
}

internal fun TypeParameterContainer.parameterId(
    typeParameters: Collection<MTypeParameter>, parent: MElement?, id: Int,
): MTypeParameter {
    if (typeParameters.isEmpty()) {
        if (parent is TypeParameterContainer) return parent.getTypeParameter(id)
        error("type parameter $id not found in $this which inside of $parent")
    }
    return typeParameters.first { it.asKm.id == id }
}
