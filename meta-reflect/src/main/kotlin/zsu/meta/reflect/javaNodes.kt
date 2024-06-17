package zsu.meta.reflect

import zsu.cacheable.Cacheable
import zsu.meta.reflect.impl.j.MJTypeImpl
import java.lang.reflect.Parameter
import java.lang.reflect.TypeVariable

interface JavaElement<T : Any> : JavaReflectAdapter<T>

class MJTypeParameter(
    override val asJr: TypeVariable<*>,
) : MTypeParameter, JavaElement<TypeVariable<*>>

class MJValueParameter(
    override val asJr: Parameter,
) : MValueParameter, JavaElement<Parameter> {
    override val name: String = asJr.name
    override val type: MType
        @Cacheable get() = MJTypeImpl(asJr.parameterizedType)

    override val varargElementType: MType?
        @Cacheable get() {
            if (asJr.isVarArgs) return null
            return (type.arguments.firstOrNull() as? MTypeNoVariance)?.type
        }
}

class MJTypeParameterClassifier(
    override val asJr: TypeVariable<*>
) : MTypeParameterClassifier, JavaElement<TypeVariable<*>> {
    val name: String = asJr.name
}

class MJClassClassifier(
    override val asJr: Class<*>,
) : MClassClassifier, JavaElement<Class<*>> {
    override val jName: JClassName = asJr.name
}
