package zsu.meta.reflect.impl.j

import zsu.cacheable.Cacheable
import zsu.meta.reflect.*
import java.lang.reflect.Method
import kotlin.metadata.ExperimentalContextReceivers

class MJFunctionImpl(
    override val parent: MClassLike,
    override val asJr: Method,
) : MFunction {
    override val name: String = asJr.name

    override val typeParameters: List<MTypeParameter>
        @Cacheable get() = asJr.typeParameters.map { MJTypeParameter(it) }

    override val valueParameters: List<MValueParameter>
        @Cacheable get() = asJr.parameters.map { MJValueParameter(it) }

    override val returnType: MType
        @Cacheable get() = MJTypeImpl(asJr.genericReturnType)

    // not support in java
    @ExperimentalContextReceivers
    override val contextReceiverTypes: List<MType> = emptyList()
    override val receiverType: MType? = null
}