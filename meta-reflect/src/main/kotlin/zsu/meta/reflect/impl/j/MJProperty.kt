package zsu.meta.reflect.impl.j

import zsu.cacheable.Cacheable
import zsu.meta.reflect.*
import java.lang.reflect.Field
import kotlin.metadata.ExperimentalContextReceivers

class MJProperty(
    override val parent: MClassLike?,
    override val asJr: Field,
) : MProperty {
    override val name: String = asJr.name
    override val returnType: MType @Cacheable get() = MJTypeImpl(asJr.genericType)

    // not support in java
    override val typeParameters: List<MTypeParameter> = emptyList()
    override val receiverType: MType? = null

    @ExperimentalContextReceivers
    override val contextReceiverTypes: List<MType> = emptyList()
    override val setterParameter: MValueParameter? = null
}