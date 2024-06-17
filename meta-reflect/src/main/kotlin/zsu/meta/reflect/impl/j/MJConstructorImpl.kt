package zsu.meta.reflect.impl.j

import zsu.cacheable.Cacheable
import zsu.meta.reflect.MClassLike
import zsu.meta.reflect.MConstructor
import zsu.meta.reflect.MJValueParameter
import zsu.meta.reflect.MValueParameter
import java.lang.reflect.Constructor

class MJConstructorImpl(
    override val asJr: Constructor<*>,
    override val parent: MClassLike?
) : MConstructor {
    override val valueParameters: List<MValueParameter>
        @Cacheable get() = asJr.parameters.map { MJValueParameter(it) }
}