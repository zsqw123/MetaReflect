package zsu.meta.reflect

@PublishedApi
internal inline val JClassName.jClass: Class<*> get() = Class.forName(this)
