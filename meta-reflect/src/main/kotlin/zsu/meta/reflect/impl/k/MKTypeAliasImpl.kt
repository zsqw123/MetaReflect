package zsu.meta.reflect.impl.k

import kotlin.metadata.KmTypeAlias
import zsu.meta.reflect.MClassLike
import zsu.meta.reflect.MKTypeParameter
import zsu.meta.reflect.MTypeAlias
import zsu.meta.reflect.impl.parameterId

internal class MKTypeAliasImpl(
    override val parent: MClassLike,
    override val asKm: KmTypeAlias,
) : MTypeAlias, MKTypeParameterContainer {
    override val typeParameters: List<MKTypeParameter> by lazy {
        asKm.typeParameters.map { MKTypeParameter(it, this) }
    }

    /** @see [KmTypeAlias.underlyingType] */
    override val underlyingType: MKTypeImpl by lazy { MKTypeImpl(asKm.underlyingType, this) }

    /** @see [KmTypeAlias.expandedType] */
    override val expandedType: MKTypeImpl by lazy { MKTypeImpl(asKm.expandedType, this) }

    override fun getTypeParameter(id: Int): MKTypeParameter {
        return parameterId(parent, id)
    }
}
