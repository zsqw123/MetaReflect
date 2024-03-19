package zsu.meta.reflect

import kotlinx.metadata.ClassName
import kotlinx.metadata.jvm.toJvmInternalName

// eg. foo.bar.Sample$Inner
typealias JClassName = String

typealias SimpleName = String

val ClassName.asJClass get() = toJvmInternalName().replace('/', '.')
