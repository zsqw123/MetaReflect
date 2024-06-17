package zsu.meta.reflect.impl.k

import kotlinx.metadata.ExperimentalContextReceivers
import kotlinx.metadata.KmClass
import zsu.cacheable.Cacheable
import zsu.meta.reflect.*
import zsu.meta.reflect.impl.parameterId

/**
 * An enhancement wrapper for [Class] through kotlin metadata
 */
internal class MKClassImpl(
    private val metaReflect: MReflect,
    override val asJr: Class<*>,
    override val asKm: KmClass,
) : AbsKMDeclaration<KmClass>(), MKClass, MKTypeParameterContainer {
    override val jName: JClassName = asKm.name.asJClass
    override val typeParameters: List<MKTypeParameter>
        @Cacheable get() = asKm.typeParameters.map { MKTypeParameter(it, this) }

    override val supertypes: List<MKTypeImpl>
        @Cacheable get() = asKm.supertypes.map { MKTypeImpl(it, this) }

    override val constructors: List<MConstructor>
        @Cacheable get() = asKm.constructors.map { MKConstructorImpl(this, it) }


    override val companionObjectName: SimpleName? = asKm.companionObject
    override val nestedClassNames: List<SimpleName> = asKm.nestedClasses
    override val enumEntryNames: List<SimpleName> = asKm.enumEntries

    override val sealedSubclassNames: List<JClassName>
        @Cacheable get() = asKm.sealedSubclasses.map { it.asJClass }

    override val inlineClassUnderlyingPropertyName: SimpleName? = asKm.inlineClassUnderlyingPropertyName
    override val inlineClassUnderlyingType: MKTypeImpl? = asKm.inlineClassUnderlyingType?.let { MKTypeImpl(it, this) }

    @ExperimentalContextReceivers
    override val contextReceiverTypes: List<MKTypeImpl>
        @Cacheable get() = asKm.contextReceiverTypes.map { MKTypeImpl(it, this) }

    override val sealedSubclasses: List<MClass>
        @Cacheable get() = sealedSubclassNames.map { it.asMClass() }


    override val companionObjectClass: MClass?
        @Cacheable get() {
            companionObjectName ?: return null
            return asJr.declaredClasses.first { it.name == companionObjectName }.asMClass()
        }

    override val nestedClasses: List<MClass>
        @Cacheable get() = asJr.declaredClasses.map { it.asMClass() }

    override fun getTypeParameter(id: Int): MKTypeParameter {
        return parameterId(null, id)
    }

    override fun toString(): String {
        return "class $jName"
    }

    private fun Class<*>.asMClass(): MClass = metaReflect.mClassFrom(this) as MClass
    private fun JClassName.asMClass(): MClass = metaReflect.mClassFrom(this.jClass) as MClass
}