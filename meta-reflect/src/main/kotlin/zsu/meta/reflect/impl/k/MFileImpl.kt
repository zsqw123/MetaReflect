package zsu.meta.reflect.impl.k

import kotlinx.metadata.KmPackage
import zsu.meta.reflect.MFile
import zsu.meta.reflect.impl.AbsKMDeclaration
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KDeclarationContainer

internal class MFileImpl(
    override val asJr: Class<*>,
    override val asKm: KmPackage,
) : AbsKMDeclaration<KmPackage>(), MFile {
    override val asKr: KDeclarationContainer by lazy {
        Reflection.getOrCreateKotlinPackage(asJr)
    }
}
