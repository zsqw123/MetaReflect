package zsu.meta.reflect.impl

import zsu.meta.reflect.MClassLike
import java.lang.reflect.Field
import java.lang.reflect.Method

internal abstract class AbsMDeclaration : MClassLike {
    override val methods: Array<Method> by lazy { asJr.declaredMethods }
    override val fields: Array<Field> by lazy { asJr.declaredFields }
    override val enumEntries: Array<Enum<*>> by lazy {
        val entries = asJr.enumConstants
        @Suppress("UNCHECKED_CAST")
        if (entries == null) emptyArray() else entries as Array<Enum<*>>
    }
    override val annotations: Array<Annotation> by lazy {
        asJr.declaredAnnotations
    }
}

