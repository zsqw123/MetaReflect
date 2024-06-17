package zsu.meta.reflect

import kotlin.metadata.ClassName
import kotlin.metadata.jvm.toJvmInternalName

// eg. foo.bar.Sample$Inner
typealias JClassName = String

typealias SimpleName = String

val ClassName.asJClass get() = toJvmInternalName().replace('/', '.')
