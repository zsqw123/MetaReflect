package zsu.meta.reflect.impl

import kotlinx.metadata.KmDeclarationContainer
import zsu.meta.reflect.MClassLike
import zsu.meta.reflect.MFunction
import zsu.meta.reflect.MProperty
import zsu.meta.reflect.MTypeAlias
import java.lang.reflect.Field
import java.lang.reflect.Method

abstract class AbsMDeclaration<T : KmDeclarationContainer> : MClassLike<T> {
    override val functions: List<MFunction> by lazy { asKm.functions.map { MFunction(this, it) } }
    override val properties: List<MProperty> by lazy { asKm.properties.map { MProperty(this, it) } }
    override val typeAliases: List<MTypeAlias> by lazy { asKm.typeAliases.map { MTypeAlias(this, it) } }

    override val methods: Array<Method> by lazy { asJr.declaredMethods }
    override val fields: Array<Field> by lazy { asJr.declaredFields }
}

