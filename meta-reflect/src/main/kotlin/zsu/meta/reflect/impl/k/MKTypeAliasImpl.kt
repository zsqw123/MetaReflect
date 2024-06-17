package zsu.meta.reflect.impl.k

import zsu.cacheable.Cacheable
import zsu.meta.reflect.MClassLike
import zsu.meta.reflect.MKTypeParameter
import zsu.meta.reflect.MTypeAlias
import zsu.meta.reflect.impl.parameterId
import kotlin.metadata.KmTypeAlias

internal class MKTypeAliasImpl(
    override val parent: MClassLike,
    override val asKm: KmTypeAlias,
) : MTypeAlias, MKTypeParameterContainer {
    override val typeParameters: List<MKTypeParameter>
        @Cacheable get() = asKm.typeParameters.map { MKTypeParameter(it, this) }

    /** @see [KmTypeAlias.underlyingType] */
    override val underlyingType: MKTypeImpl
        @Cacheable get() = MKTypeImpl(asKm.underlyingType, this)

    /** @see [KmTypeAlias.expandedType] */
    override val expandedType: MKTypeImpl
        @Cacheable get() = MKTypeImpl(asKm.expandedType, this)

    override fun getTypeParameter(id: Int): MKTypeParameter {
        return parameterId(parent, id)
    }
}
