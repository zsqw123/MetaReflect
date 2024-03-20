package zsu.meta.reflect.impl.j

import kotlinx.metadata.ExperimentalContextReceivers
import zsu.meta.reflect.*
import java.lang.reflect.Method

class MJFunctionImpl(
    override val parent: MClassLike,
    override val asJr: Method,
) : MFunction {
    override val name: String = asJr.name

    override val typeParameters: List<MTypeParameter> by lazy {
        asJr.typeParameters.map { MJTypeParameter(it) }
    }

    override val valueParameters: List<MValueParameter> by lazy {
        asJr.parameters.map { MJValueParameter(it) }
    }

    override val returnType: MType by lazy {
        MJTypeImpl(asJr.genericReturnType)
    }

    // not support in java
    @ExperimentalContextReceivers
    override val contextReceiverTypes: List<MType> = emptyList()
    override val receiverType: MType? = null
}