package zsu.meta.reflect.impl.k

import kotlin.metadata.KmConstructor
import kotlin.metadata.jvm.signature
import zsu.meta.reflect.MKConstructor
import zsu.meta.reflect.MKValueParameter
import zsu.meta.reflect.MValueParameter
import java.lang.reflect.Constructor
import org.objectweb.asm.Type as AsmType

internal class MKConstructorImpl(
    override val parent: MKClassImpl,
    override val asKm: KmConstructor,
) : MKConstructor {
    override val valueParameters: List<MValueParameter> by lazy {
        asKm.valueParameters.map { MKValueParameter(it, parent) }
    }
    override val asJr: Constructor<*> by lazy {
        val parentClass = parent.asJr
        val descriptor = asKm.signature?.descriptor ?: throw IllegalStateException(
            "cannot read signature of $asKm. class: ${parentClass.name}"
        )
        parent.asJr.declaredConstructors.first {
            AsmType.getConstructorDescriptor(it) == descriptor
        }
    }
}
