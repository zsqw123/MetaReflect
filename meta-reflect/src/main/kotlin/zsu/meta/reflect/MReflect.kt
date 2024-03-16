package zsu.meta.reflect

import zsu.meta.reflect.impl.MReflectImpl
import zsu.meta.reflect.impl.PreloadStub

interface MReflect {
    /**
     * @param fallbackRuntime true if you want read metadata at runtime. set to false if you
     *  only wants to read [MClass] by generated codes. (only supports `true` currently :)
     * @throws IllegalArgumentException if this class is not a kotlin class or didn't contain
     *  kotlin metadata or unsupported kotlin metadata format.
     */
    fun mClassFrom(jClass: Class<*>, fallbackRuntime: Boolean = true): MetadataContainer

    /**
     * @see mClassFrom
     */
    fun mClassFrom(jClassName: JClassName, fallbackRuntime: Boolean = true): MetadataContainer {
        return mClassFrom(Class.forName(jClassName), fallbackRuntime)
    }

    companion object {
        private val emptyMReflect: MReflect by lazy { MReflectImpl(emptyList()) }

        /**
         * get [MReflect] instance from generated mapping (can be empty)
         */
        fun get(allMapping: Collection<MReflectGeneratedMapping> = emptyList()): MReflect {
            return if (allMapping.isEmpty()) emptyMReflect else MReflectImpl(allMapping)
        }

        /**
         * preload meta reflect classes to make fist time reflect much faster.
         */
        fun preload() {
            emptyMReflect.mClassFrom(PreloadStub::class.java)
        }
    }
}

interface MReflectGeneratedMapping {
    val names: List<JClassName>
    fun getMetadataByName(name: JClassName): Metadata
}

inline fun <reified T> MReflect.mClass(fallbackRuntime: Boolean = true): MClass {
    return mClassFrom(T::class.java, fallbackRuntime) as MClass
}
