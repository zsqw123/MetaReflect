package zsu.meta.reflect.impl

import kotlinx.metadata.KmDeclarationContainer
import zsu.meta.reflect.*
import java.lang.reflect.Field
import java.lang.reflect.Method


internal abstract class AbsMDeclaration : MClassLike {
    override val methods: Array<Method> by lazy { asJr.declaredMethods }
    override val fields: Array<Field> by lazy { asJr.declaredFields }
}

internal abstract class AbsKMDeclaration<T : KmDeclarationContainer> : AbsMDeclaration(), KtElement<T> {
    override val functions: List<MFunction> by lazy { asKm.functions.map { MKFunctionImpl(this, it) } }
    override val properties: List<MProperty> by lazy { asKm.properties.map { MProperty(this, it) } }
    override val typeAliases: List<MTypeAlias> by lazy { asKm.typeAliases.map { MTypeAlias(this, it) } }
}
