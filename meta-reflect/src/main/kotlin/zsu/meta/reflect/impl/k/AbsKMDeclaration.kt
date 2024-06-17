package zsu.meta.reflect.impl.k

import zsu.cacheable.Cacheable
import zsu.meta.reflect.KtElement
import zsu.meta.reflect.MFunction
import zsu.meta.reflect.MProperty
import zsu.meta.reflect.MTypeAlias
import zsu.meta.reflect.impl.AbsMDeclaration
import kotlin.metadata.KmDeclarationContainer

internal abstract class AbsKMDeclaration<T : KmDeclarationContainer> : AbsMDeclaration(), KtElement<T> {
    override val functions: List<MFunction>
        @Cacheable get() = asKm.functions.map { MKFunctionImpl(this, it) }
    override val properties: List<MProperty>
        @Cacheable get() = asKm.properties.map { MKPropertyImpl(this, it) }
    override val typeAliases: List<MTypeAlias>
        @Cacheable get() = asKm.typeAliases.map { MKTypeAliasImpl(this, it) }
}
