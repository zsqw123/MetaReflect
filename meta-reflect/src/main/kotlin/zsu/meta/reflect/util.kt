package zsu.meta.reflect

import kotlinx.metadata.ClassName
import kotlinx.metadata.jvm.toJvmInternalName

typealias JClassName = String

val ClassName.asJClass get() = toJvmInternalName().replace('/', '.')
