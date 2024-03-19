package zsu.meta.reflect

import java.lang.reflect.TypeVariable

interface JavaElement<T : Any> : JavaReflectAdapter<T>

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
