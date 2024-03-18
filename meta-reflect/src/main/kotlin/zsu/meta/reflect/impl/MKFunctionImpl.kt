package zsu.meta.reflect.impl

import kotlinx.metadata.ExperimentalContextReceivers
import kotlinx.metadata.KmFunction
import kotlinx.metadata.jvm.signature
import zsu.meta.reflect.*
import java.lang.reflect.Method
import org.objectweb.asm.Type as AsmType

/**
 * @param parent returns null when function is a lambda generated.
 */
internal class MKFunctionImpl(
    override val parent: MClassLike?,
    override val asKm: KmFunction,
) : MKFunction {
    override val name = asKm.name
    override val typeParameters: List<MTypeParameter> by lazy {
        asKm.typeParameters.map { MTypeParameter(it, this) }
    }
    override val receiverType: MType? = asKm.receiverParameterType?.let { MType(it, this) }

    @ExperimentalContextReceivers
    override val contextReceiverTypes: List<MType> by lazy {
        asKm.contextReceiverTypes.map { MType(it, this) }
    }

    override val valueParameters: List<MValueParameter> by lazy {
        asKm.valueParameters.map { MValueParameter(it) }
    }

    override val returnType: MType = MType(asKm.returnType, this)

    // null when lambda
    override val asJr: Method? by lazy {
        val parentClass = parent?.asJr ?: return@lazy null
        val descriptor = asKm.signature?.descriptor ?: throw IllegalStateException(
            "cannot read signature of $asKm. class: ${parentClass.name}"
        )
        parentClass.declaredMethods.first {
            AsmType.getMethodDescriptor(it) == descriptor
        }
    }

    override fun getTypeParameter(id: Int): MTypeParameter {
        return parameterId(typeParameters, parent, id)
    }

    override fun toString(): String {
        return "function $name"
    }
}