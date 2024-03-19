package zsu.meta.reflect.impl.j

import zsu.meta.reflect.*
import java.lang.reflect.*

internal class MJTypeImpl(
    override val asJr: Type,
) : MType {
    override val arguments: List<MTypeProjection> by lazy {
        val args = when (val javaType = asJr) {
            is GenericArrayType -> arrayOf(javaType.genericComponentType)
            is ParameterizedType -> javaType.actualTypeArguments
            else -> return@lazy emptyList()
        }
        args.map { it.asMTypeProjection() }
    }

    override val classifier: MClassifier by lazy {
        val classifier: MClassifier? = when (val javaType = asJr) {
            is Class<*> -> MJClassClassifier(javaType)
            is GenericArrayType -> MJClassClassifier(Array::class.java)
            is ParameterizedType -> (javaType.rawType as? Class<*>)?.let(::MJClassClassifier)
            is TypeVariable<*> -> MJTypeParameterClassifier(javaType)
            else -> null
        }
        classifier ?: error("unknown type when building MClassifier: $asJr, typeName: ${asJr.typeName}")
    }

    private fun Type.asMTypeProjection(): MTypeProjection {
        if (this !is WildcardType) return MTypeNoVariance(MJTypeImpl(this))
        val upper = upperBounds.firstOrNull()
        val lower = lowerBounds.firstOrNull()
        val variance = when {
            upper != null -> MVariance.OUT
            lower != null -> MVariance.IN
            else -> return MStarType
        }
        val mjType = MJTypeImpl(upper ?: lower ?: return MStarType)
        return MTypeWithVariance(variance, mjType)
    }
}
