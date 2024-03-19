package zsu.meta.reflect.impl.type

import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * copied from kotlin stdlib [kotlin.reflect.WildcardTypeImpl]
 */
internal class WildcardTypeImpl(private val upperBound: Type?, private val lowerBound: Type?) : WildcardType {
    override fun getUpperBounds(): Array<Type> = arrayOf(upperBound ?: Any::class.java)

    override fun getLowerBounds(): Array<Type> = if (lowerBound == null) emptyArray() else arrayOf(lowerBound)

    override fun getTypeName(): String = when {
        lowerBound != null -> "? super ${typeToString(lowerBound)}"
        upperBound != null && upperBound != Any::class.java -> "? extends ${typeToString(upperBound)}"
        else -> "?"
    }

    override fun equals(other: Any?): Boolean =
        other is WildcardType && upperBounds.contentEquals(other.upperBounds) && lowerBounds.contentEquals(other.lowerBounds)

    override fun hashCode(): Int =
        upperBounds.contentHashCode() xor lowerBounds.contentHashCode()

    override fun toString(): String = getTypeName()

    companion object {
        val STAR = WildcardTypeImpl(null, null)
    }
}

private fun typeToString(type: Type) {
    if (type is Class<*>) {
        if (type.isArray) {
            val unwrap = generateSequence(type, Class<*>::getComponentType)
            unwrap.last().name + "[]".repeat(unwrap.count())
        } else type.name
    } else type.toString()
}
