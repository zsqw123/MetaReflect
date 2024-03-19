package zsu.meta.reflect.impl.k

import zsu.meta.reflect.MKTypeParameter

interface MKTypeParameterContainer {
    val typeParameters: List<MKTypeParameter>
    fun getTypeParameter(id: Int): MKTypeParameter
}
