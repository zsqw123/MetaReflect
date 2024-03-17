package zsu.meta.reflect.impl

import kotlinx.metadata.ExperimentalContextReceivers
import kotlinx.metadata.KmClass
import zsu.meta.reflect.*

/**
 * An enhancement wrapper for [Class] through kotlin metadata
 */
internal class MClassImpl(
    override val asJr: Class<*>,
    override val asKm: KmClass,
) : AbsMDeclaration<KmClass>(), MClass, TypeParameterContainer {
    override val jName: JClassName = asKm.name.asJClass
    override val typeParameters: List<MTypeParameter> by lazy {
        asKm.typeParameters.map { MTypeParameter(it, this) }
    }

    override val supertypes: List<MType> by lazy {
        asKm.supertypes.map { MType(it, this) }
    }

    override val constructors: List<MConstructor> by lazy {
        asKm.constructors.map { MConstructor(this, it) }
    }

    override val companionObjectName: String? = asKm.companionObject
    override val nestedClassNames: List<String> = asKm.nestedClasses
    override val enumEntryNames: List<String> = asKm.enumEntries

    override val sealedSubclassNames: List<JClassName> by lazy {
        asKm.sealedSubclasses.map { it.asJClass }
    }

    override val inlineClassUnderlyingPropertyName: String? = asKm.inlineClassUnderlyingPropertyName
    override val inlineClassUnderlyingType: MType? = asKm.inlineClassUnderlyingType?.let { MType(it, this) }

    @ExperimentalContextReceivers
    override val contextReceiverTypes: List<MType> by lazy {
        asKm.contextReceiverTypes.map { MType(it, this) }
    }

    override val sealedSubclasses: List<Class<*>> by lazy {
        sealedSubclassNames.map(JClassName::jClass)
    }

    override val companionObjectClass: Class<*>? by lazy {
        companionObjectName?.let { "$jName\$$it".jClass }
    }

    override val nestedClasses: List<Class<*>> by lazy {
        nestedClassNames.map { "$jName\$$it".jClass }
    }

    override fun getTypeParameter(id: Int): MTypeParameter {
        return parameterId(typeParameters, null, id)
    }

    override fun toString(): String {
        return "class $jName"
    }
}