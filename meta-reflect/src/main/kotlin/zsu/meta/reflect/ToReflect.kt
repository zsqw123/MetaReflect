package zsu.meta.reflect

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type
import kotlin.reflect.*

interface ReflectAdapter

interface JavaReflectAdapter<out J> : ReflectAdapter {
    /** transform this object to java reflection object. */
    val asJr: J
}

interface KReflectAdapter<out K> : ReflectAdapter {
    /**
     * transform this object to kotlin reflection object.
     * Limited in no kotlin-reflect dependency mode (such as cannot create [KFunction] or [KProperty])
     */
    val asKr: K
}

interface JavaClassReflectAdapter : JavaReflectAdapter<Class<*>>
interface JavaConstructorReflectAdapter : JavaReflectAdapter<Constructor<*>>
interface JavaMethodReflectAdapter : JavaReflectAdapter<Method?>
interface JavaFieldReflectAdapter : JavaReflectAdapter<Field>

interface KTypeAdapter : KReflectAdapter<KType>, JavaReflectAdapter<Type> {
    @OptIn(ExperimentalStdlibApi::class)
    override val asJr: Type get() = asKr.javaType
}

interface KClassAdapter : KReflectAdapter<KClass<*>>, JavaClassReflectAdapter {
    override val asKr: KClass<*> get() = asJr.kotlin
}

interface KFileAdapter : KReflectAdapter<KDeclarationContainer>, JavaClassReflectAdapter

