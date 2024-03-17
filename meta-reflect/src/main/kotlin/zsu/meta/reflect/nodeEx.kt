package zsu.meta.reflect

import java.lang.reflect.Field
import java.lang.reflect.Method

inline val MClass.sealedSubclasses: List<Class<*>> get() = sealedSubclassNames.map(JClassName::jClass)

inline val MClass.companionObjectClass: Class<*>? get() = companionObjectName?.let { "$jName\$$it".jClass }

inline val MClass.nestedClasses: List<Class<*>> get() = nestedClassNames.map { "$jName\$$it".jClass }

inline val MClass.methods: Array<Method> get() = asJr.declaredMethods

inline val MClass.fields: Array<Field> get() = asJr.declaredFields

@PublishedApi
internal inline val JClassName.jClass: Class<*> get() = Class.forName(this)
