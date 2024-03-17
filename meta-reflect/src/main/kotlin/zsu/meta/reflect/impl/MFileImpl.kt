package zsu.meta.reflect.impl

import kotlinx.metadata.KmPackage
import zsu.meta.reflect.MFile
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KDeclarationContainer

class MFileImpl(
    override val asJr: Class<*>,
    override val asKm: KmPackage,
) : AbsMDeclaration<KmPackage>(), MFile {
    override val asKr: KDeclarationContainer by lazy {
        Reflection.getOrCreateKotlinPackage(asJr)
    }
}
