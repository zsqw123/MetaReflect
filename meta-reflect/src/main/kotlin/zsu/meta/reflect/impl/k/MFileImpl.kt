package zsu.meta.reflect.impl.k

import kotlinx.metadata.KmPackage
import zsu.cacheable.Cacheable
import zsu.meta.reflect.MFile
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KDeclarationContainer

internal class MFileImpl(
    override val asJr: Class<*>,
    override val asKm: KmPackage,
) : AbsKMDeclaration<KmPackage>(), MFile {
    override val asKr: KDeclarationContainer
        @Cacheable get() = Reflection.getOrCreateKotlinPackage(asJr)
}
