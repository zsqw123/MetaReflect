package zsu.meta.reflect

inline val MClass.sealedSubclasses: List<Class<*>> get() = sealedSubclassNames.map(JClassName::jClass)

inline val MClass.companionObjectClass: Class<*>? get() = companionObjectName?.let { "$jName\$$it".jClass }

inline val MClass.nestedClasses: List<Class<*>> get() = nestedClassNames.map { "$jName\$$it".jClass }

@PublishedApi
internal inline val JClassName.jClass: Class<*> get() = Class.forName(this)
