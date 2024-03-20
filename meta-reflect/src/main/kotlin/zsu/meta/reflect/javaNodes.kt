package zsu.meta.reflect

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
    override val type: MType by lazy {
        MJTypeImpl(asJr.parameterizedType)
    }

    override val varargElementType: MType? by lazy {
        if (asJr.isVarArgs) return@lazy null
        (type.arguments.firstOrNull() as? MTypeNoVariance)?.type
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
