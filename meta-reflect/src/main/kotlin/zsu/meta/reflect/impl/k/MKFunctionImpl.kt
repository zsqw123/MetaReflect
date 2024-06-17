package zsu.meta.reflect.impl.k

import zsu.cacheable.Cacheable
import zsu.meta.reflect.*
import zsu.meta.reflect.impl.parameterId
import java.lang.reflect.Method
import kotlin.metadata.ExperimentalContextReceivers
import kotlin.metadata.KmFunction
import kotlin.metadata.jvm.signature
import org.objectweb.asm.Type as AsmType

/**
 * @param parent returns null when function is a lambda generated.
 */
internal class MKFunctionImpl(
    override val parent: MClassLike?,
    override val asKm: KmFunction,
) : MKFunction, MKTypeParameterContainer {
    override val name = asKm.name
    override val typeParameters: List<MKTypeParameter>
        @Cacheable get() = asKm.typeParameters.map { MKTypeParameter(it, this) }
    override val receiverType: MType? = asKm.receiverParameterType?.let { MKTypeImpl(it, this) }

    @ExperimentalContextReceivers
    override val contextReceiverTypes: List<MKTypeImpl>
        @Cacheable get() = asKm.contextReceiverTypes.map { MKTypeImpl(it, this) }

    override val valueParameters: List<MKValueParameter>
        @Cacheable get() = asKm.valueParameters.map { MKValueParameter(it, this) }

    override val returnType: MKTypeImpl = MKTypeImpl(asKm.returnType, this)

    // null when lambda
    override val asJr: Method?
        @Cacheable get() {
            val parentClass = parent?.asJr ?: return null
            val descriptor = asKm.signature?.descriptor ?: throw IllegalStateException(
                "cannot read signature of $asKm. class: ${parentClass.name}"
            )
            return parentClass.declaredMethods.first {
                AsmType.getMethodDescriptor(it) == descriptor
            }
        }

    override fun getTypeParameter(id: Int): MKTypeParameter {
        return parameterId(parent, id)
    }

    override fun toString(): String {
        return "function $name"
    }
}