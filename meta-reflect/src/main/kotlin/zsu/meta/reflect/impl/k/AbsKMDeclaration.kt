package zsu.meta.reflect.impl.k

import kotlinx.metadata.KmDeclarationContainer
import zsu.meta.reflect.KtElement
import zsu.meta.reflect.MFunction
import zsu.meta.reflect.MProperty
import zsu.meta.reflect.MTypeAlias
import zsu.meta.reflect.impl.AbsMDeclaration

internal abstract class AbsKMDeclaration<T : KmDeclarationContainer> : AbsMDeclaration(), KtElement<T> {
    override val functions: List<MFunction> by lazy { asKm.functions.map { MKFunctionImpl(this, it) } }
    override val properties: List<MProperty> by lazy { asKm.properties.map { MKPropertyImpl(this, it) } }
    override val typeAliases: List<MTypeAlias> by lazy { asKm.typeAliases.map { MKTypeAliasImpl(this, it) } }
}
