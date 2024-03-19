package zsu.meta.reflect.impl.j

import kotlinx.metadata.ExperimentalContextReceivers
import zsu.meta.reflect.*
import zsu.meta.reflect.impl.AbsMDeclaration

internal class MJClassImpl(
    private val metaReflect: MReflect,
    override val asJr: Class<*>,
) : AbsMDeclaration(), MClass {
    override val jName: JClassName = asJr.name
    override val typeParameters: List<MTypeParameter>
        get() = TODO("Not yet implemented")
    override val supertypes: List<MType> by lazy {
        listOf(asJr.genericSuperclass, *asJr.genericInterfaces).map { MJTypeImpl(it) }
    }
    override val constructors: List<MConstructor>
        get() = TODO("Not yet implemented")

    override val nestedClasses: List<MClass> by lazy {
        asJr.declaredClasses.map { metaReflect.mClassFrom(it) as MClass }
    }
    override val nestedClassNames: List<SimpleName> by lazy {
        asJr.declaredClasses.map { it.simpleName }
    }

    override val functions: List<MFunction>
        get() = TODO("Not yet implemented")
    override val properties: List<MProperty>
        get() = TODO("Not yet implemented")

    override val enumEntryNames: List<SimpleName> by lazy { enumEntries.map { it.name } }

    /** todo: didn't support now, but may support in higher java version */
    override val sealedSubclassNames: List<JClassName> = emptyList()
    override val sealedSubclasses: List<MClass> = emptyList()

    // not support in java
    @ExperimentalContextReceivers
    override val contextReceiverTypes: List<MType> = emptyList()
    override val companionObjectName: SimpleName? = null
    override val inlineClassUnderlyingPropertyName: SimpleName? = null
    override val inlineClassUnderlyingType: MType? = null
    override val companionObjectClass: MClass? = null
    override val typeAliases: List<MTypeAlias> = emptyList()
}