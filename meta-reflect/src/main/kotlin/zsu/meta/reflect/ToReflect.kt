package zsu.meta.reflect

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.KDeclarationContainer
import kotlin.reflect.KType
import kotlin.reflect.javaType

interface ReflectAdapter

interface JavaReflectAdapter<out J> : ReflectAdapter {
    val asJr: J
}

interface KReflectAdapter<out K> : ReflectAdapter {
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

interface KFileAdapter : KReflectAdapter<KDeclarationContainer>, JavaClassReflectAdapter {
    override val asKr: KDeclarationContainer get() = Reflection.getOrCreateKotlinPackage(asJr)
}

