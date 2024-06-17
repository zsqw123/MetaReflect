package zsu.meta.reflect.impl.j

import zsu.cacheable.Cacheable
import kotlin.metadata.ExperimentalContextReceivers
import zsu.meta.reflect.*
import zsu.meta.reflect.impl.AbsMDeclaration

internal class MJClassImpl(
    private val metaReflect: MReflect,
    override val asJr: Class<*>,
) : AbsMDeclaration(), MClass {
    override val jName: JClassName = asJr.name
    override val typeParameters: List<MTypeParameter>
        @Cacheable get() = asJr.typeParameters.map { MJTypeParameter(it) }

    override val supertypes: List<MType>
        @Cacheable get() = listOf(asJr.genericSuperclass, *asJr.genericInterfaces).map { MJTypeImpl(it) }

    override val constructors: List<MConstructor>
        @Cacheable get() = asJr.declaredConstructors.map { MJConstructorImpl(it, this) }

    override val nestedClasses: List<MClass>
        @Cacheable get() = asJr.declaredClasses.map { metaReflect.mClassFrom(it) as MClass }

    override val nestedClassNames: List<SimpleName>
        @Cacheable get() = asJr.declaredClasses.map { it.simpleName }

    override val functions: List<MFunction>
        @Cacheable get() = asJr.declaredMethods.map { MJFunctionImpl(this, it) }

    override val properties: List<MProperty>
        @Cacheable get() = asJr.declaredFields.map { MJProperty(this, it) }

    override val enumEntryNames: List<SimpleName> @Cacheable get() = enumEntries.map { it.name }

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