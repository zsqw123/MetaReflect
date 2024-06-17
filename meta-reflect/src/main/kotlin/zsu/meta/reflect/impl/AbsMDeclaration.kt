package zsu.meta.reflect.impl

import zsu.cacheable.Cacheable
import zsu.meta.reflect.MClassLike
import java.lang.reflect.Field
import java.lang.reflect.Method

internal abstract class AbsMDeclaration : MClassLike {
    override val methods: Array<Method> @Cacheable get() = asJr.declaredMethods
    override val fields: Array<Field> @Cacheable get() = asJr.declaredFields
    override val enumEntries: Array<Enum<*>>
        @Cacheable get() {
            val entries = asJr.enumConstants
            @Suppress("UNCHECKED_CAST")
            return if (entries == null) emptyArray() else entries as Array<Enum<*>>
        }

    override val annotations: Array<Annotation> @Cacheable get() = asJr.declaredAnnotations
}

