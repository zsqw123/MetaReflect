package zsu.meta.reflect.impl.k

import kotlinx.metadata.ExperimentalContextReceivers
import kotlinx.metadata.KmClass
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
    override val typeParameters: List<MKTypeParameter> by lazy {
        asKm.typeParameters.map { MKTypeParameter(it, this) }
    }

    override val supertypes: List<MKTypeImpl> by lazy {
        asKm.supertypes.map { MKTypeImpl(it, this) }
    }

    override val constructors: List<MConstructor> by lazy {
        asKm.constructors.map { MKConstructorImpl(this, it) }
    }

    override val companionObjectName: String? = asKm.companionObject
    override val nestedClassNames: List<String> = asKm.nestedClasses
    override val enumEntryNames: List<String> = asKm.enumEntries

    override val sealedSubclassNames: List<JClassName> by lazy {
        asKm.sealedSubclasses.map { it.asJClass }
    }

    override val inlineClassUnderlyingPropertyName: String? = asKm.inlineClassUnderlyingPropertyName
    override val inlineClassUnderlyingType: MKTypeImpl? = asKm.inlineClassUnderlyingType?.let { MKTypeImpl(it, this) }

    @ExperimentalContextReceivers
    override val contextReceiverTypes: List<MKTypeImpl> by lazy {
        asKm.contextReceiverTypes.map { MKTypeImpl(it, this) }
    }

    override val sealedSubclasses: List<MClass> by lazy {
        sealedSubclassNames.map { it.asMClass() }
    }

    override val companionObjectClass: MClass? by lazy {
        companionObjectName ?: return@lazy null
        asJr.declaredClasses.first { it.name == companionObjectName }.asMClass()
//        companionObjectName?.let { "$jName\$$it" }?.asMClass()
    }

    override val nestedClasses: List<MClass> by lazy {
        asJr.declaredClasses.map { it.asMClass() }
//        nestedClassNames.map { "$jName\$$it".asMClass() }
    }

    override fun getTypeParameter(id: Int): MKTypeParameter {
        return parameterId(null, id)
    }

    override fun toString(): String {
        return "class $jName"
    }

    private fun Class<*>.asMClass(): MClass = metaReflect.mClassFrom(this) as MClass
    private fun JClassName.asMClass(): MClass = metaReflect.mClassFrom(this.jClass) as MClass
}