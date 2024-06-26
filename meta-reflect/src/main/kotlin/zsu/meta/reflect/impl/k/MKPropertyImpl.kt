package zsu.meta.reflect.impl.k

import zsu.cacheable.Cacheable
import kotlin.metadata.ExperimentalContextReceivers
import kotlin.metadata.KmProperty
import kotlin.metadata.jvm.fieldSignature
import zsu.meta.reflect.*
import zsu.meta.reflect.impl.parameterId
import java.lang.reflect.Field

internal class MKPropertyImpl(
    override val parent: MClassLike,
    override val asKm: KmProperty,
) : MKProperty, MKTypeParameterContainer {
    override val name = asKm.name

    override val typeParameters: List<MKTypeParameter>
        @Cacheable get() = asKm.typeParameters.map { MKTypeParameter(it, this) }
    override val receiverType: MKTypeImpl? = asKm.receiverParameterType?.let { MKTypeImpl(it, this) }

    @ExperimentalContextReceivers
    override val contextReceiverTypes: List<MKTypeImpl>
        @Cacheable get() = asKm.contextReceiverTypes.map { MKTypeImpl(it, this) }

    override val setterParameter: MKValueParameter? = asKm.setterParameter?.let { MKValueParameter(it, this) }
    override val returnType: MKTypeImpl = MKTypeImpl(asKm.returnType, this)

    override val asJr: Field
        @Cacheable get() {
            val parentClass = parent.asJr
            val jvmName = asKm.fieldSignature?.name ?: throw IllegalStateException(
                "cannot read signature of $asKm. class: ${parentClass.name}"
            )
            return parentClass.declaredFields.first { it.name == jvmName }
        }

    override fun getTypeParameter(id: Int): MKTypeParameter {
        return parameterId( parent, id)
    }

    override fun toString(): String {
        return "property $name"
    }
}